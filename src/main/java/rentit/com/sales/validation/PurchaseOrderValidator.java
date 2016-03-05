package rentit.com.sales.validation;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import rentit.com.sales.domain.PurchaseOrder;
import rentit.com.sales.domain.PurchaseOrder.POStatus;

public class PurchaseOrderValidator implements Validator{
	
	private final BusinessPeriodValidator periodValidator;
	
	public PurchaseOrderValidator(BusinessPeriodValidator periodValidator){
		this.periodValidator = Objects.requireNonNull(periodValidator);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == PurchaseOrder.class;
	}

	@Override
	public void validate(Object object, Errors errors) {
		PurchaseOrder po = (PurchaseOrder)object;
		if(po.getId() == 0L)
			errors.rejectValue("id", "Purchase order id is not set!");
		if(po.getPlantEntryId() == 0L)
			errors.rejectValue("plantEntryId", "Plant entry id in purchase order is not set!");

		if(po.getRentalPeriod() == null){
			errors.rejectValue("rental period", "Rental period cannot be null!");
		}else{
			try{
				errors.pushNestedPath("businessPeriod");
				ValidationUtils.invokeValidator(this.periodValidator, po.getRentalPeriod(), errors);
			} finally {
				errors.popNestedPath();
			}
		}
		
		if(po.getStatus() == POStatus.OPEN){
			if(po.getReservationId() == null)
				errors.rejectValue("reservationId", "Reservation id is not set!");

			if(po.getTotal() == null)
				errors.rejectValue("total", "Total price can't be null!");
			else if(po.getTotal().compareTo(BigDecimal.ZERO) < 0)
				errors.rejectValue("total", "Total price can't be negative!");
		}
	}
}
