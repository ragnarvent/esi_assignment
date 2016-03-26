package rentit.com.maintenance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import rentit.com.maintenance.domain.model.MaintenancePlan;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long>, QueryDslPredicateExecutor<MaintenancePlan> {
	
}
