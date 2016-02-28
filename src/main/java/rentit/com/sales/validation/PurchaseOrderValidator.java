package rentit.com.sales.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rentit.com.sales.domain.PurchaseOrder;

public class PurchaseOrderValidator implements Validator{

	@Override
	public boolean supports(Class<?> classObject) {
		return classObject == PurchaseOrder.class;
	}

	@Override
	public void validate(Object object, Errors errors) {
		PurchaseOrder po = (PurchaseOrder)object;
		//TODO:Validate fields
	}
}
