package rentit.com.sales.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rentit.com.sales.domain.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}
