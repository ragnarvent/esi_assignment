package rentit.com.inventory.domain.model;


import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(exclude={"items"})
@ToString(exclude="items")
@PersistenceCapable
@Table(name="plant_inventory_entry")
public class PlantInvEntry {
	
	@Id
	private long id;
	
	private String name;
	
	private String description;
	
	@Column(precision = 8, scale = 2)
	private BigDecimal price;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "plantInfo")
	private List<PlantInvItem> items;
	
}
