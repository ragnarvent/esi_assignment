package math.ut.ee.models;


import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@PersistenceCapable
@Table(name="plant_reservation")
public class PlantReservation {
	
	@Id
	@GeneratedValue
	private long id;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
	@OneToOne
	private PlantInvItem plant;
	
	@OneToOne(optional=true)
	@JoinColumn(name="maintplan_id")
	private MaintenancePlan maintPlan;
	
	@OneToOne(optional=true)
	private PurchaseOrder rental;
}
