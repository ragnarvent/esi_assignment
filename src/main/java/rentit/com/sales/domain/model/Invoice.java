package rentit.com.sales.domain.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Invoice {
	public static enum InvoiceStatus{
		SENT, PAID;
	}
	
	@Id
	private long id;
	
	@Column(precision = 8, scale = 2)
	private BigDecimal total;
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;
	
	@Column(name="po_id")
	private long poId;
	
	public static Invoice of(long id, long poId, BigDecimal total){
		Invoice invoice = new Invoice();
		invoice.setId(id);
		invoice.setPoId(poId);
		invoice.setTotal(total);
		invoice.setStatus(InvoiceStatus.SENT);
		return invoice;
	}
}
