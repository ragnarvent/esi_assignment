package rentit.com.inventory.domain;


import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import rentit.com.common.domain.BusinessPeriod;
import rentit.com.maintenance.domain.MaintenancePlan;

@Entity
@Data
@PersistenceCapable
@Table(name="plant_reservation")
public class PlantReservation {
	
	@Id
	private long id;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
	@OneToOne
	private PlantInvItem plant;
	
	@OneToOne(optional=true)
	@JoinColumn(name="maintplan_id")
	private MaintenancePlan maintPlan;
	
	@ManyToOne(optional=true)
	@Column(name="rental_id")
	private Long rentalId;
	
	public static PlantReservation of(long id, PlantInvItem plantItem, BusinessPeriod rentalPeriod){
		PlantReservation r = new PlantReservation();
		r.setId(id);
		r.setPlant(plantItem);
		r.setRentalPeriod(rentalPeriod);
		return r;
	}
}
