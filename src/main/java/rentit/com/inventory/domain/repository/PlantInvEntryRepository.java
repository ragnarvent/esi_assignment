package rentit.com.inventory.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import rentit.com.inventory.domain.model.PlantInvEntry;

@Repository
public interface PlantInvEntryRepository extends JpaRepository<PlantInvEntry, Long>, QueryDslPredicateExecutor<PlantInvEntry> {

	public List<PlantInvEntry> findByNameContaining(String string);
	
	@Query("select distinct p.plantInfo from PlantInvItem p where LOWER(p.plantInfo.name) like LOWER(CONCAT('%', ?1, '%')) and p.id not in (select t.plant.id from PlantReservation t where "
			+ "( ?2 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate ) or ( ?3 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate )) ")
	public List<PlantInvEntry> findAvailablePlants( String name, LocalDate startDate, LocalDate endDate);
	
}
