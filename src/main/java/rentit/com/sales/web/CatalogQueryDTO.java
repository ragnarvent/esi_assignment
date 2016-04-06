package rentit.com.sales.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import rentit.com.common.application.dto.BusinessPeriodDTO;

@Data
@NoArgsConstructor
public class CatalogQueryDTO {
	private String name;
	private BusinessPeriodDTO rentalPeriod;
}
