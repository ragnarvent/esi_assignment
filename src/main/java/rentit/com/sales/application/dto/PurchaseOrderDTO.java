package rentit.com.sales.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import rentit.com.common.application.dto.BusinessPeriodDTO;
import rentit.com.common.rest.ResourceSupport;
import rentit.com.inventory.application.dto.PlantInvEntryDTO;
import rentit.com.sales.domain.model.PurchaseOrder.POStatus;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class PurchaseOrderDTO extends ResourceSupport{
	private PlantInvEntryDTO plant;
	private BigDecimal cost;
	private POStatus status;
	private BusinessPeriodDTO rentalPeriod;
}
