package rentit.com.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface PlantReservationRepository extends JpaRepository<PlantReservation, Long>, QueryDslPredicateExecutor<PlantReservation>{
	
}
