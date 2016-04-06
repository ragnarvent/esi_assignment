package rentit.com.common.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import rentit.com.common.application.dto.BusinessPeriodDTO;

@Embeddable
@Value
@NoArgsConstructor(force=true,access=lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(staticName="of")
public class BusinessPeriod {
	
	@Column(name="START_DATE")
	private LocalDate startDate;
	
	@Column(name="END_DATE")
	private LocalDate endDate;
	
	public static BusinessPeriod fromDto(BusinessPeriodDTO period){
		return BusinessPeriod.of(period.getStartDate(), period.getEndDate());
	}
}