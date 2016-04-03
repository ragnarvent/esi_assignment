package rentit.com.inventory.domain.model;


import java.math.BigDecimal;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@PersistenceCapable
@Table(name="plant_inventory_entry")
public class PlantInvEntry {
	
	@Id
	private long id;
	
	private String name;
	
	private String description;
	
	@Column(precision = 8, scale = 2)
	private BigDecimal price;
	
}
