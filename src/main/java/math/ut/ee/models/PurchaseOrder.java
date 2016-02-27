package math.ut.ee.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="purchase_order")
public class PurchaseOrder {

	public static enum POStatus {
		PENDING, REJECTED, OPEN, CLOSED;
	}

	@Id
	@GeneratedValue
	private long id;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "rental")
	private List<PlantReservation> reservations;
	
	@OneToOne
	private PlantInvEntry plant;

	private LocalDate issueDate;
	
	private LocalDate paymentSchedule;

	@Column(precision = 8, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	private POStatus status;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
}
