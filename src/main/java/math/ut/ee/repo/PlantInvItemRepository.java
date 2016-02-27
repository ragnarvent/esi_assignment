package math.ut.ee.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import math.ut.ee.models.PlantInvItem;

@Repository
public interface PlantInvItemRepository extends JpaRepository<PlantInvItem, Long>, QueryDslPredicateExecutor<PlantInvItem> {
	
	@Query("select p from PlantInvItem p where p.id = ?1 and p.condition='SERVICEABLE'")
	public PlantInvItem queryServiceablePlantById( String id );
	
	@Query("select p from PlantInvItem p where p not in (select t.plant from PlantReservation t where t.rentalPeriod.startDate > ?1 )")
	public List<PlantInvItem> queryUnhiredPlant(LocalDate date);
}
