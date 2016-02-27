package math.ut.ee.models;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import math.ut.ee.RentitApplication;
import math.ut.ee.models.PlantInvItem.EquipmentCondition;
import math.ut.ee.repo.MaintenancePlanRepository;
import math.ut.ee.repo.MaintenanceTaskRepository;
import math.ut.ee.repo.PlantInvEntryRepository;
import math.ut.ee.repo.PlantInvItemRepository;
import math.ut.ee.repo.PlantReservationRepository;
import math.ut.ee.repo.PurchaseOrderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@Sql(scripts = "plants-dataset.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PlantCatalogTests {
	
	@Autowired
	PlantInvEntryRepository plantRepo;
	
	@Autowired
	PlantReservationRepository plantReserve;
	
	@Autowired
	PurchaseOrderRepository poRepo;
	
	@Autowired
	MaintenancePlanRepository maintRepo;
	
	@Autowired
	MaintenanceTaskRepository taskRepo;
	
	@Autowired
	PlantInvItemRepository plantInvRepo;
	
	@Test
	public void queryPlantCatalog() {
		assertThat(plantRepo.count(), is(14l));
	}

	@Test
	public void queryByName() {
		assertThat(plantRepo.findByNameContaining("Mini").size(), is(2));
	}

	@Test
	public void findAvailableByNameAndPeriodTest() {
		PlantInvEntry p = plantRepo.findOne(1l);
		
		final BusinessPeriod rentalPeriod = BusinessPeriod.of(LocalDate.of(2016, 2, 20), LocalDate.of(2016, 2, 25));
		
		assertThat(plantRepo.findAvailablePlants("Mini", rentalPeriod.getStartDate(), rentalPeriod.getEndDate()), hasItem(p));

		//Create new purchase order
		PurchaseOrder po = new PurchaseOrder();
		po.setPlant(p);
		po.setRentalPeriod(rentalPeriod);
		poRepo.save(po);
		
		//Create Reserve period
		PlantReservation pReserve = new PlantReservation();
		pReserve.setPlant(p.getItems().get(0));
		pReserve.setRental(po);
		pReserve.setRentalPeriod(rentalPeriod);
		plantReserve.save(pReserve);
		
		assertThat(plantRepo.findAvailablePlants("Mini",rentalPeriod.getStartDate(), rentalPeriod.getEndDate()), not(hasItem(p)));
	}
	
	@Test
	public void isPlantServicableStrictTest() {
		assertNotNull(plantInvRepo.queryServiceablePlantById("1"));//Exists + is serviceable
		assertNull(plantInvRepo.queryServiceablePlantById("25"));
		
		PlantInvItem item = new PlantInvItem();
		item.setSerialNumber("25");
		item.setCondition(EquipmentCondition.INCOMPLETE);
		plantInvRepo.save(item);

		assertNull(plantInvRepo.queryServiceablePlantById("25"));
	}
	
	@Test
	public void unhiredPlantTest() {
		List<PlantInvItem> items = plantInvRepo.queryUnhiredPlant(LocalDate.now().minusMonths(6));
		assertThat(items.size(), is(13)); // from total of 14 plants 1 plant was serviced in the past 6 months
	}
}
