package rentit.com.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CatalogQueryDTO {
	private String name;
	private BusinessPeriodDTO rentalPeriod;
}
