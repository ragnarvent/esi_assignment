package rentit.com.web.dto;

import lombok.Data;

@Data
public class CatalogQueryDTO {
	
	private String name;
	
	private BusinessPeriodDTO rentalPeriod;

}
