package rentit.com.web.dto;

import java.math.BigDecimal;

import org.springframework.hateoas.ResourceSupport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import rentit.com.sales.domain.PurchaseOrder.POStatus;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class PurchaseOrderDTO extends ResourceSupport{
	private long plantId;
	private long poId;
	private String name;
	private String description;
	private BigDecimal cost;
	private POStatus status;
	private BusinessPeriodDTO rentalPeriod;
}
