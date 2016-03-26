package rentit.com.inventory.domain.model;


import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
	
	@OneToOne
	private PlantInvItem plant;
	
	@Column(name="maintplan_id")
	private Long maintPlanId; 
	
	@Column(name="rental_id")
	private Long rentalId;
	
	public static PlantReservation of(long id, PlantInvItem plantItem, BusinessPeriod rentalPeriod){
		PlantReservation r = new PlantReservation();
		r.setId(id);
		r.setPlant(plantItem);
		r.setRentalPeriod(rentalPeriod);
		return r;
	}
	
	public BigDecimal calculateTotalCost() {
		//End exclusive, so add +1
		return plant.getPlantInfo().getPrice().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(rentalPeriod.getStartDate(), rentalPeriod.getEndDate())+1));
	}
}
