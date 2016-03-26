package rentit.com.sales.application.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.application.service.IdentifierFactoryService;
import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.common.exceptions.InvalidFieldException;
import rentit.com.common.exceptions.PlantNotFoundException;
import rentit.com.common.exceptions.PurchaseOrderNotFoundException;
import rentit.com.inventory.application.service.InventoryService;
import rentit.com.inventory.domain.model.PlantReservation;
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
	
	public Collection<PurchaseOrder> fetchAllPOs(){
		return poRepo.findAll();
	}
	
	public PurchaseOrder fetchPurchaseOrder( long id ) throws PurchaseOrderNotFoundException{
		PurchaseOrder order = poRepo.findOne(id);
		if( order == null )
			throw new PurchaseOrderNotFoundException(id);
		return order;
	}
	
	public PurchaseOrder createAndProcessPO(long plantId, BusinessPeriod rentalPeriod) throws InvalidFieldException, PlantNotFoundException{
		PurchaseOrder po = PurchaseOrder.of(idFactory.nextPurchaseOrderID(), plantId, rentalPeriod);
		
		validatePO(po, false); // pre-validate
		
		poRepo.save(po); //Save valid PO
		
		reservePO(po);	
		
		validatePO(po, true); // post-validate 
		
		return poRepo.save(po); //Save PO after successful reservation
	}

	private void reservePO(PurchaseOrder po) throws PlantNotFoundException {
		final PlantReservation reservation = inventoryService.reservePlant(po.getId(), po.getPlantEntryId(),po.getRentalPeriod());
		if( reservation == null ){ //In case no plant was available, save PO with rejected status
			po.setStatus(POStatus.REJECTED);
			poRepo.save(po);
			throw new PlantNotFoundException(po.getId()); 
		}else {
			po.setReservationId(reservation.getId());
			po.setTotal(reservation.calculateTotalCost());
		}
	}

	private static void validatePO(PurchaseOrder po, boolean isPostValidate) throws InvalidFieldException {
		DataBinder binder = new DataBinder(po);
		binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator(), new ContactPersonValidator(), isPostValidate));
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage()); //Alternatively we could list all the errors in PO fields
		}
	}

	public PurchaseOrder modifyPO(long poId, BusinessPeriod rentalPeriod) throws PurchaseOrderNotFoundException, PlantNotFoundException, InvalidFieldException {
		PurchaseOrder existingPO = fetchPurchaseOrder(poId);
		if (existingPO.getStatus() != POStatus.REJECTED) 
			throw new PurchaseOrderNotFoundException(poId);
		
		existingPO.setRentalPeriod(rentalPeriod);
		existingPO.setStatus(POStatus.PENDING);
		
		validatePO(existingPO, false);
		
		reservePO(existingPO);
		
		return poRepo.save(existingPO);
	}
	
	public PurchaseOrder modifyPoState(long poId, POStatus newStatus) throws PurchaseOrderNotFoundException{
		PurchaseOrder existingPO = fetchPurchaseOrder(poId);
		existingPO.setStatus(newStatus);
		return poRepo.save(existingPO);
	}
}
