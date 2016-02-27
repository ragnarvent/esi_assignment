package rentit.com.common;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force=true,access=lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(staticName="of")
public class BusinessPeriod {
	
	@Column(name="START_DATE")
	private LocalDate startDate;
	
	@Column(name="END_DATE")
	private LocalDate endDate;
}