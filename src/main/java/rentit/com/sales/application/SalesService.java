package rentit.com.sales.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.InvalidException;
import rentit.com.common.domain.BusinessPeriod;
import rentit.com.inventory.application.InventoryService;
import rentit.com.inventory.domain.PlantReservation;
import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.sales.domain.PurchaseOrder.POStatus;
import rentit.com.sales.domain.PurchaseOrderRepository;
import rentit.com.sales.validation.PurchaseOrderValidator;

@Service
public class SalesService {
	
	@Autowired
	IdentifierFactory idFactory;
	
	@Autowired
	InventoryService inventoryService;

	@Autowired
	PurchaseOrderRepository poRepo;
	
	public PurchaseOrder createAndProcessPO(long plantId, BusinessPeriod rentalPeriod) throws InvalidException {
		PurchaseOrder po = PurchaseOrder.of(idFactory.nextPurchaseOrderID(), plantId, rentalPeriod);
		
		validatePO(po);
		
		poRepo.save(po); //Save valid PO

		reservePO(po);
		
		poRepo.save(po); //Save PO after successful reservation
		
		return po;
	}

	private void reservePO(PurchaseOrder po) throws InvalidException {
		final PlantReservation reservation = inventoryService.reservePlant(po.getId(), po.getPlantEntryId(),po.getRentalPeriod());
		po.getReservations().add(reservation);
		po.setTotal(reservation.getPlant().getPlantInfo().getPrice()); //In the simplified scenario we reserve only one plant, thus the price of one plant
		po.setStatus(POStatus.OPEN);
	}

	private void validatePO(PurchaseOrder po) throws InvalidException {
		DataBinder binder = new DataBinder(po);
		binder.addValidators(new PurchaseOrderValidator());
		binder.validate();
		
		if(binder.getBindingResult().hasErrors()){
			throw new InvalidException(binder.getBindingResult().getFieldError().toString()); //Alternatively we could list all the errors in PO fields
		}
	}
}
