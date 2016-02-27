package math.ut.ee.models;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@PersistenceCapable
@Table(name="maint_plan")
public class MaintenancePlan {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(name = "year_of_action")
	private int yearOfAction;
	
	@OneToOne
	private PlantInvItem item;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name="maint_plan_id", referencedColumnName="id")
	private List<MaintenanceTask> tasks;
	
}
