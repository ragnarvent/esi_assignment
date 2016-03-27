package rentit.com.common.application.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rentit.com.common.domain.model.BusinessPeriod;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class BusinessPeriodDTO {
	
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate;
    
	public static BusinessPeriodDTO toDto(BusinessPeriod period){
		BusinessPeriodDTO dto = new BusinessPeriodDTO();
		dto.setStartDate(period.getStartDate());
		dto.setEndDate(period.getEndDate());
		return dto;
	}
}
