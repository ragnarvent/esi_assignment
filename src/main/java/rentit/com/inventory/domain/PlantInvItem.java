package rentit.com.inventory.domain;


import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@PersistenceCapable
@Table(name="plant_inventory_item")
public class PlantInvItem {
	
	public static enum EquipmentCondition{
		SERVICEABLE,REPAIRABLE,INCOMPLETE,CONDEMNED;
	}
	
	@Id
	@Column(name="id")
	private String serialNumber;
	
	@Enumerated(EnumType.STRING)
	private EquipmentCondition condition;
	
	@OneToOne
	@JoinColumn(name="PLANTINFO_ID")
	private PlantInvEntry plantInfo;

}
