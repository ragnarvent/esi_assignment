package rentit.com.common.domain.validation;

import java.time.LocalDate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rentit.com.common.domain.model.BusinessPeriod;

public class BusinessPeriodValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == BusinessPeriod.class;
	}

	@Override
	public void validate(Object object, Errors errors) {
		BusinessPeriod period = (BusinessPeriod)object;
		final LocalDate now = LocalDate.now();
		if(period.getStartDate().isBefore(now))
			errors.rejectValue("startDate", "startDate.past", "Start date can't be in the past!");
		if(period.getEndDate().isBefore(now))
			errors.rejectValue("endDate", "endDate.past", "End date can't be in the past!");
		if(period.getEndDate().isBefore(period.getStartDate()))
			errors.rejectValue("startEndDate", "startDate.beforeEndDate", "End date can't be before start date!");
	}
}
