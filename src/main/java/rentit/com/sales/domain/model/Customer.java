package rentit.com.sales.domain.model;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="customer")
public class Customer {
	
	@Id
	@GeneratedValue
	private long id;
	
	@OneToMany( mappedBy = "customer" )
	private List<PurchaseOrder> orders;
	
	@Embedded
	private List<ContactPerson> representatives;
}
