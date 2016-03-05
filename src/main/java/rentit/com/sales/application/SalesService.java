package rentit.com.sales.application;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.exceptions.InvalidFieldException;
import rentit.com.exceptions.PlantNotFoundException;
import rentit.com.exceptions.PurchaseOrderNotFoundException;
import rentit.com.inventory.application.InventoryService;
import rentit.com.inventory.domain.PlantReservation;
import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.sales.domain.PurchaseOrder.POStatus;
import rentit.com.sales.domain.PurchaseOrderRepository;
import rentit.com.sales.validation.BusinessPeriodValidator;
import rentit.com.sales.validation.PurchaseOrderValidator;

@Service
public class SalesService {
	
	@Autowired
	private IdentifierFactory idFactory;
	
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
		
		validatePO(po); // pre-validate
		
		poRepo.save(po); //Save valid PO
		
		reservePO(po);	
		
		validatePO(po); // post-validate 
		
		poRepo.save(po); //Save PO after successful reservation
		
		return po;
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
			po.setStatus(POStatus.OPEN);
		}
	}

	private void validatePO(PurchaseOrder po) throws InvalidFieldException {
		DataBinder binder = new DataBinder(po);
		binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator()));
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage()); //Alternatively we could list all the errors in PO fields
		}
	}

	public PurchaseOrder modifyPO(long poId, BusinessPeriod rentalPeriod) throws PurchaseOrderNotFoundException, PlantNotFoundException {
		PurchaseOrder existingPO = fetchPurchaseOrder(poId);
		if (existingPO.getStatus() != POStatus.REJECTED) 
			throw new PurchaseOrderNotFoundException(poId);
		
		existingPO.setRentalPeriod(rentalPeriod);

		reservePO(existingPO);
		
		poRepo.save(existingPO);
		
		return existingPO;
	}
}
