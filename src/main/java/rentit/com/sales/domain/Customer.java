package rentit.com.sales.domain;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
	private List<PurchaseOrder> orders;
	
	@Embedded
	private List<ContactPerson> representatives;
}
