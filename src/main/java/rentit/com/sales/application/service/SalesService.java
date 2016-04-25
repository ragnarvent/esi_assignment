package rentit.com.sales.application.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.application.service.IdentifierFactoryService;
import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.common.exceptions.ExtensionNotFound;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.inventory.application.service.InventoryService;
import rentit.com.inventory.application.service.PlantCatalogService;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.model.PlantReservation;
import rentit.com.sales.application.dto.PurchaseOrderAssembler;
import rentit.com.sales.application.dto.PurchaseOrderDTO;
import rentit.com.sales.domain.model.Extension;
import rentit.com.sales.domain.model.PurchaseOrder;
import rentit.com.sales.domain.model.PurchaseOrder.POStatus;
import rentit.com.sales.domain.repository.PurchaseOrderRepository;
import rentit.com.sales.domain.validation.ContactPersonValidator;
import rentit.com.sales.domain.validation.PurchaseOrderValidator;

@Service
public class SalesService {
	
	@Autowired
	private IdentifierFactoryService idFactory;
	
	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private PurchaseOrderRepository poRepo;
	
	@Autowired
	private PlantCatalogService catalogService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private PurchaseOrderAssembler poAssembler;
	
	public Collection<PurchaseOrderDTO> fetchAllPOs(){
		return poAssembler.toResources(poRepo.findAll());
	}
	
	private PurchaseOrder findOrder(long id) throws PurchaseOrderNotFoundException{
		PurchaseOrder order = poRepo.findOne(id);
		if( order == null )
			throw new PurchaseOrderNotFoundException(id);
		return order;
	}
	
	public PurchaseOrderDTO fetchPurchaseOrder( long id ) throws PurchaseOrderNotFoundException{
		return poAssembler.toResource(findOrder(id));
	}
	
	public PurchaseOrder findPoFullRepresentation(PurchaseOrderDTO poDto) throws PurchaseOrderNotFoundException {
	    long id = poAssembler.resolveId(Objects.requireNonNull(poDto.getLink("self")));
	    return findOrder(id);
	}
	
	public PurchaseOrderDTO createAndProcessPO(PurchaseOrderDTO partialPoDto) throws InvalidFieldException, PlantNotFoundException{
		PlantInvEntry plantEntry = catalogService.findPlantFullRepresentation(partialPoDto.getPlant());
		
		PurchaseOrder po = PurchaseOrder.of(idFactory.nextPurchaseOrderID(), plantEntry.getId(), BusinessPeriod.fromDto(partialPoDto.getRentalPeriod()));
		
		validatePO(po, false); // pre-validate
		
		poRepo.save(po); //Save valid PO
		
		reservePO(po, plantEntry);	
		
		validatePO(po, true); // post-validate 
		
		return poAssembler.toResource(poRepo.save(po)); //Save PO after successful reservation
	}

	private void reservePO(PurchaseOrder po, PlantInvEntry plantEntry) throws PlantNotFoundException {
		final PlantReservation reservation = inventoryService.reservePlant(po.getId(), po.getPlantEntryId(),po.getRentalPeriod());
		if( reservation == null ){
			po.setStatus(POStatus.REJECTED);
			poRepo.save(po);
			throw new PlantNotFoundException(po.getId(), poAssembler.toResource(po).getLink("self").getHref());
		}
		
		po.setReservationId(reservation.getId());
		
		po.setTotal(plantEntry.getPrice().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(po.getRentalPeriod().getStartDate(), po.getRentalPeriod().getEndDate())+1)));
	}

	private static void validatePO(PurchaseOrder po, boolean isPostValidate) throws InvalidFieldException {
		DataBinder binder = new DataBinder(po);
		binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator(), new ContactPersonValidator(), isPostValidate));
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage()); //Alternatively we could list all the errors in PO fields
		}
	}

	public PurchaseOrderDTO modifyPO(PurchaseOrderDTO partialPoDto) throws PurchaseOrderNotFoundException, PlantNotFoundException, InvalidFieldException {
		PurchaseOrder existingPO = findPoFullRepresentation(partialPoDto);
		if (existingPO.getStatus() != POStatus.REJECTED) //Treat it as not found
			throw new PurchaseOrderNotFoundException(existingPO.getId());
		
		existingPO.setRentalPeriod(BusinessPeriod.fromDto(partialPoDto.getRentalPeriod()));
		existingPO.setStatus(POStatus.PENDING_CONFIRMATION);
		
		validatePO(existingPO, false);
		
		reservePO(existingPO, catalogService.findPlantFullRepresentation(partialPoDto.getPlant()));
		
		return poAssembler.toResource(poRepo.save(existingPO));
	}
	
	public PurchaseOrderDTO modifyPoState(long poId, POStatus newStatus) throws PurchaseOrderNotFoundException{
		PurchaseOrder existingPO = findOrder(poId);
		existingPO.setStatus(newStatus);
		return poAssembler.toResource(poRepo.save(existingPO));
	}

	public PurchaseOrderDTO extendPoRentalPeriod(Long poId, LocalDate newEndDate) throws PurchaseOrderNotFoundException {
		PurchaseOrder existingPO = findOrder(poId);
		existingPO.setExtension(Extension.of(idFactory.nextPoExtensionID(), newEndDate));
		existingPO.setStatus(POStatus.PENDING_EXTENSION);
		return poAssembler.toResource(poRepo.save(existingPO));
	}

	public PurchaseOrderDTO handleExtension(Long poId, Long eid, boolean accept) throws ExtensionNotFound, PurchaseOrderNotFoundException {
		PurchaseOrder existingPO = findOrder(poId);
		if(!existingPO.getExtension().getExtensionId().equals(eid))
			throw new ExtensionNotFound(poId, eid);
		
		if(accept)
			existingPO.setRentalPeriod(BusinessPeriod.of(existingPO.getRentalPeriod().getStartDate(), existingPO.getExtension().getNewEndDate()));
		
		existingPO.setExtension(null);
		existingPO.setStatus(POStatus.OPEN);
		return poAssembler.toResource(poRepo.save(existingPO));
	}

	public MimeMessage createInvoiceFromPo(Long poId) throws PurchaseOrderNotFoundException, MessagingException, IOException {
		PurchaseOrderDTO existingPO = fetchPurchaseOrder(poId);
		return invoiceService.composeMail(poId, existingPO.getLink("self").getHref(), existingPO.getCost());
	}
}
