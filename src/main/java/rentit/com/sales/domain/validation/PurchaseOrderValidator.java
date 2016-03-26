package rentit.com.sales.domain.validation;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.sales.domain.model.PurchaseOrder;

public class PurchaseOrderValidator implements Validator{
	
	private final BusinessPeriodValidator periodValidator;
	private final ContactPersonValidator personValidator;
	private final boolean isPostValidate;
	
	public PurchaseOrderValidator(BusinessPeriodValidator periodValidator, ContactPersonValidator personValidator, boolean isPostValidate){
		this.periodValidator = Objects.requireNonNull(periodValidator);
		this.personValidator = Objects.requireNonNull(personValidator);
		this.isPostValidate = isPostValidate;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == PurchaseOrder.class;
	}

	@Override
	public void validate(Object object, Errors errors) {
		PurchaseOrder po = (PurchaseOrder)object;
		if(po.getId() == 0L)
			errors.rejectValue("id", "purchaseOrderId.NotSet", "Purchase order id is not set!");
		if(po.getPlantEntryId() == 0L)
			errors.rejectValue("plantEntryId", "plantEntryId.NotSet", "Plant entry id in purchase order is not set!");

		if(po.getRentalPeriod() == null){
			errors.rejectValue("rental period", "rentalPeriod.Null", "Rental period cannot be null!");
		}else{
			try{
				errors.pushNestedPath("rentalPeriod");
				ValidationUtils.invokeValidator(this.periodValidator, po.getRentalPeriod(), errors);
			} finally {
				errors.popNestedPath();
			}
		}
		
		if( po.getContact() != null ){
			try{
				errors.pushNestedPath("contact");
				ValidationUtils.invokeValidator(this.personValidator, po.getContact(), errors);
			} finally {
				errors.popNestedPath();
			}
		}
		
		if(isPostValidate){
			if(po.getReservationId() == null)
				errors.rejectValue("reservationId", "reservationId.NotSet","Reservation id is not set!");

			if(po.getTotal() == null)
				errors.rejectValue("total", "price.Null", "Total price can't be null!");
			else if(po.getTotal().compareTo(BigDecimal.ZERO) < 0)
				errors.rejectValue("total", "price.Negative", "Total price can't be negative!");
		}
	}
}
