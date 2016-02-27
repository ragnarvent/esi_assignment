package math.ut.ee.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import math.ut.ee.models.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}
