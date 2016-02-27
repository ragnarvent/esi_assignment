package rentit.com.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import rentit.com.maintenance.domain.MaintenanceTask;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long>, QueryDslPredicateExecutor<MaintenanceTask> {	
}
