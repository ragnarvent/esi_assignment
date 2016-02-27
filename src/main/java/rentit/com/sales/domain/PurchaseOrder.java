package rentit.com.sales.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import rentit.com.common.BusinessPeriod;
import rentit.com.inventory.domain.PlantInvEntry;
import rentit.com.inventory.domain.PlantReservation;

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

	@ManyToOne
	private Customer customer;
	
	private LocalDate issueDate;
	
	private LocalDate paymentSchedule;

	@Column(precision = 8, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	private POStatus status;
	
	@Embedded
	private Address address;
	
	@Embedded
	private Comment comment;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
}
