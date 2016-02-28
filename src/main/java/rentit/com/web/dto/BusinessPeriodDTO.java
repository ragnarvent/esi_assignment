package rentit.com.web.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName="of")
public class BusinessPeriodDTO {
	private LocalDate startDate;
	private LocalDate endDate;
}
