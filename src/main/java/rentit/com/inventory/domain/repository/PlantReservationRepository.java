package rentit.com.inventory.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import rentit.com.inventory.domain.model.PlantReservation;

public interface PlantReservationRepository extends JpaRepository<PlantReservation, Long>, QueryDslPredicateExecutor<PlantReservation>{
	
}
