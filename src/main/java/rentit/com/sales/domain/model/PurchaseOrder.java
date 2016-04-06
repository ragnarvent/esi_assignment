package rentit.com.sales.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import rentit.com.common.domain.model.BusinessPeriod;

@Entity
@Data
@Table(name="purchase_order")
public class PurchaseOrder {

	public static enum POStatus {
		PENDING_CONFIRMATION, PENDING_EXTENSION, REJECTED, OPEN, CLOSED;
	}

	@Id
	private long id;
	
	@Column(name="reservation_id")
	private Long reservationId;
	
	@Column(name="plant_entry_id")
	private long plantEntryId;

	private LocalDate issueDate;
	
	private LocalDate paymentSchedule;

	@Column(precision = 8, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	private POStatus status;
	
	@Embedded
	private ContactPerson contact;
	
	@Embedded
	private List<Comment> notes;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
	@Embedded
	private Extension extension;
	
	public static PurchaseOrder of(long id, long plantEntryId, BusinessPeriod rentalPeriod){
		PurchaseOrder po = new PurchaseOrder();
		po.setId(id);
		po.setPlantEntryId(plantEntryId);
		po.setRentalPeriod(rentalPeriod);
		po.setStatus(POStatus.PENDING_CONFIRMATION);
		po.setIssueDate(LocalDate.now());
		return po;
	}
}
