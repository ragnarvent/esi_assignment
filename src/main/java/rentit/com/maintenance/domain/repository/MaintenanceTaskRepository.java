package rentit.com.maintenance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import rentit.com.maintenance.domain.model.MaintenanceTask;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long>, QueryDslPredicateExecutor<MaintenanceTask> {	
}
