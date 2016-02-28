package rentit.com.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rentit.com.sales.domain.PurchaseOrder.POStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class PurchaseOrderDTO {
	private long plantId;
	private String name;
	private String description;
	private BigDecimal cost;
	private POStatus status;
	private BusinessPeriodDTO rentalPeriod;
}
