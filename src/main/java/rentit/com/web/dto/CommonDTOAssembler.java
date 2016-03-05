package rentit.com.web.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import rentit.com.common.domain.BusinessPeriod;
import rentit.com.exceptions.InvalidFieldException;

@Service
public class CommonDTOAssembler{


	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public BusinessPeriod businessPeriodFromDTO(BusinessPeriodDTO periodDTO) throws InvalidFieldException {
		try {
			return BusinessPeriod.of(LocalDate.parse(periodDTO.getStartDate(), formatter),
					LocalDate.parse(periodDTO.getEndDate()));
		} catch (RuntimeException _ex) {
			throw new InvalidFieldException("Unable to parse business period!");
		}
	}

	public BusinessPeriodDTO businessPeriodToDTO(BusinessPeriod period) {
		return BusinessPeriodDTO.of(period.getStartDate().toString(), period.getEndDate().toString());
	}

}
