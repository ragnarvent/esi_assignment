package rentit.com.inventory.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantInvEntryRepository extends JpaRepository<PlantInvEntry, Long>, QueryDslPredicateExecutor<PlantInvEntry> {

	public List<PlantInvEntry> findByNameContaining(String string);
	
	//Considering the nature of the domain model, currently we only retrieve the entries that have at least some available equipment.
	//A separate query should be made in order to get the exact number. 
	@Query("select p.plantInfo from PlantInvItem p where p.plantInfo.name like CONCAT('%', ?1, '%') and p.id not in (select t.plant.id from PlantReservation t where "
			+ "( ?2 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate ) or ( ?3 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate )) ")
	public List<PlantInvEntry> findAvailablePlants( String name, LocalDate startDate, LocalDate endDate);
	
}
