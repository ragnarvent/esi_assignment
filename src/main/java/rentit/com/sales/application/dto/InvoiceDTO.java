package rentit.com.sales.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import rentit.com.common.rest.ResourceSupport;
import rentit.com.sales.domain.model.Invoice.InvoiceStatus;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class InvoiceDTO extends ResourceSupport{
	private BigDecimal total;
	private InvoiceStatus status;
}
