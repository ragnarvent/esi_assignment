package math.ut.ee.models;

import java.math.BigDecimal;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@PersistenceCapable
@Table(name="maint_task")
public class MaintenanceTask {
	
	public static enum TypeOfWork{
		PREVENTIVE,CORRECTIVE,OPERATIVE;
	}
	
	@Id
	@GeneratedValue
	private long id;
	
	//Added additional ID field to
	//keep the relation unidirectional
	@Column(name="maint_plan_id")
	private long maintPlanId;
	
	private String description;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
	@Column(precision = 8, scale = 2)
	private BigDecimal price;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type_of_work")
	private TypeOfWork typeOfWork;
	
	@OneToOne
	private PlantReservation reservation;
	
}
