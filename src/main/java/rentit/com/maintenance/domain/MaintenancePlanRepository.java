package rentit.com.maintenance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long>, QueryDslPredicateExecutor<MaintenancePlan> {
	
}
