package rentit.com.sales.application;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.RentitException;
import rentit.com.common.domain.BusinessPeriod;
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
	
	public PurchaseOrder fetchPurchaseOrder( long id ){
		PurchaseOrder order = poRepo.findOne(id);
		if( order == null ){
			throw new RentitException("Could not find purchase order with id: " + id);
		}
		return order;
	}
	
	public PurchaseOrder createAndProcessPO(long plantId, BusinessPeriod rentalPeriod) throws RentitException {
		PurchaseOrder po = PurchaseOrder.of(idFactory.nextPurchaseOrderID(), plantId, rentalPeriod);
		
		validatePO(po); // pre-validate
		
		poRepo.save(po); //Save valid PO

		reservePO(po);
		
		validatePO(po); // post-validate 
		
		poRepo.save(po); //Save PO after successful reservation
		
		return po;
	}

	private void reservePO(PurchaseOrder po) {
		final PlantReservation reservation = inventoryService.reservePlant(po.getId(), po.getPlantEntryId(),po.getRentalPeriod());
		po.setReservationId(reservation.getId());
		po.setTotal(reservation.calculateTotalCost());
		po.setStatus(POStatus.OPEN);
	}

	private void validatePO(PurchaseOrder po) {
		DataBinder binder = new DataBinder(po);
		binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator()));
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new RentitException(binder.getBindingResult().getFieldError().getDefaultMessage()); //Alternatively we could list all the errors in PO fields
		}
	}
}
