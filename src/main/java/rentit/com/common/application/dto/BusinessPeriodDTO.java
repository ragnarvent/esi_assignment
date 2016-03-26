package rentit.com.common.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class BusinessPeriodDTO {
	private String startDate;
	private String endDate;
}
