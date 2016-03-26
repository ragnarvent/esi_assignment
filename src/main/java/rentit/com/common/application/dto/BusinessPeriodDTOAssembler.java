package rentit.com.common.application.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.common.domain.validation.BusinessPeriodValidator;
import rentit.com.common.exceptions.InvalidFieldException;

@Service
public class BusinessPeriodDTOAssembler{


	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public BusinessPeriod businessPeriodFromDTO(BusinessPeriodDTO periodDTO) throws InvalidFieldException {
		try {
			return validateBusinessPeriod(BusinessPeriod.of(LocalDate.parse(periodDTO.getStartDate(), formatter),
					LocalDate.parse(periodDTO.getEndDate())));
		} catch (RuntimeException _ex) {
			throw new InvalidFieldException("Unable to parse business period!");
		}
	}

	public BusinessPeriodDTO businessPeriodToDTO(BusinessPeriod period) {
		return BusinessPeriodDTO.of(period.getStartDate().toString(), period.getEndDate().toString());
	}
	
	private static BusinessPeriod validateBusinessPeriod( BusinessPeriod period) throws InvalidFieldException{
		DataBinder binder = new DataBinder(period);
		binder.addValidators(new BusinessPeriodValidator());
		binder.validate();
		
		if(binder.getBindingResult().hasFieldErrors()){
			throw new InvalidFieldException(binder.getBindingResult().getFieldError().getDefaultMessage());
		}
		return period;
	}

}
