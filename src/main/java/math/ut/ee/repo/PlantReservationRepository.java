package math.ut.ee.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import math.ut.ee.models.PlantReservation;

public interface PlantReservationRepository extends JpaRepository<PlantReservation, Long>, QueryDslPredicateExecutor<PlantReservation>{
	
}
