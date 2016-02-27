package rentit.com.inventory.domain;


import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import rentit.com.common.BusinessPeriod;
import rentit.com.maintenance.domain.MaintenancePlan;
import rentit.com.sales.domain.PurchaseOrder;

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
	
	@ManyToOne(optional=true)
	private PurchaseOrder rental;
}
