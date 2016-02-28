package rentit.com.inventory.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantInvItemRepository extends JpaRepository<PlantInvItem, Long>, QueryDslPredicateExecutor<PlantInvItem> {
	
	@Query("select p from PlantInvItem p where p.id = ?1 and p.condition='SERVICEABLE'")
	public PlantInvItem queryServiceablePlantById( String id );
	
	@Query("select p from PlantInvItem p where p not in (select t.plant from PlantReservation t where t.rentalPeriod.startDate > ?1 )")
	public List<PlantInvItem> queryUnhiredPlant(LocalDate date);
	
	@Query("select p from PlantInvItem p where p.plantInfo.id = ?1 and p.id not in (select t.plant.id from PlantReservation t where "
			+ "( ?2 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate ) or ( ?3 BETWEEN t.rentalPeriod.startDate and t.rentalPeriod.endDate )) ")
	public List<PlantInvItem> findAvailablePlantItems( long plantEntryId, LocalDate startDate, LocalDate endDate);

}
