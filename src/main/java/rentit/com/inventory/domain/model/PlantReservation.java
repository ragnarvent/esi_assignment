package rentit.com.inventory.domain.model;


import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import rentit.com.common.domain.model.BusinessPeriod;

@Entity
@Data
@PersistenceCapable
@Table(name="plant_reservation")
public class PlantReservation {
	
	@Id
	private long id;
	
	@Embedded
	private BusinessPeriod rentalPeriod;
	
	@Column(name="plant_id")
	private String plantItemId;
	
	@Column(name="maintplan_id")
	private Long maintPlanId; 
	
	@Column(name="rental_id")
	private Long rentalId;
	
	public static PlantReservation of(long id, String plantItemId, BusinessPeriod rentalPeriod){
		PlantReservation r = new PlantReservation();
		r.setId(id);
		r.setPlantItemId(plantItemId);
		r.setRentalPeriod(rentalPeriod);
		return r;
	}
	
}
