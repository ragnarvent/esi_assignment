package math.ut.ee.models;

import static math.ut.ee.models.specifications.InventorySpecifications.isAvailableFor;
import static math.ut.ee.models.specifications.InventorySpecifications.isRelaxedServicableWithId;
import static math.ut.ee.models.specifications.InventorySpecifications.isServicableWithId;
import static math.ut.ee.models.specifications.InventorySpecifications.nameContains;
import static math.ut.ee.models.specifications.MaintenanceSpecifications.isWithinPastYears;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import math.ut.ee.RentitApplication;
import math.ut.ee.models.PlantInvItem.EquipmentCondition;
import math.ut.ee.repo.MaintenancePlanRepository;
import math.ut.ee.repo.MaintenanceTaskRepository;
import math.ut.ee.repo.PlantInvEntryRepository;
import math.ut.ee.repo.PlantInvItemRepository;
import math.ut.ee.repo.PlantReservationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@Sql(scripts="plants-dataset.sql")
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class PlantCatalogWithQueryDSLTests {
	@Autowired
	PlantInvEntryRepository plantRepo;
	
	@Autowired
	PlantInvItemRepository plantInvRepo;
	
	@Autowired
	MaintenancePlanRepository maintRepo;
	
	@Autowired
	MaintenanceTaskRepository taskRepo;
	
	@Autowired
	PlantReservationRepository resevRepo;

	@Test
	public void findAvailableByNameAndPeriodTest() {
		List<PlantInvEntry> entries = Lists.newArrayList(plantRepo.findAll(nameContains("Mini").and(isAvailableFor(BusinessPeriod.of(LocalDate.of(2016, 3, 22), LocalDate.of(2016, 3, 27))))));
		assertThat(entries.size(), is(2));
	}
	
	@Test
	public void correctiveRepairCountTest() {
		List<MaintenancePlan> entries = Lists.newArrayList(maintRepo.findAll(isWithinPastYears(5)));
		Map<Integer,Integer> result = entries.stream().collect(Collectors.toMap(MaintenancePlan::getYearOfAction, v->v.getTasks().size()));
		assertThat(result.get(2016), is(1));
		assertThat(result.get(2015), is(2));
		assertThat(result.get(2014), is(1));
		assertThat(result.get(2013), is(1));
		assertThat(result.get(2012), is(1));
		assertNull(result.get(2011));
	}
	
	@Test
	public void correctiveRepairSumTest() {
		//Currently all the fancy computations are done in unit tests, in reality they'd be moved to some DAO class(e.g extend the repository functionality?)
		//Or alternatively create a more customised query and move the counting/summing there. 
		List<MaintenancePlan> entries = Lists.newArrayList(maintRepo.findAll(isWithinPastYears(5)));
		Map<Integer,BigDecimal> result = entries.stream().collect(Collectors.toMap(MaintenancePlan::getYearOfAction, 
				v->v.getTasks().stream().map(MaintenanceTask::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
		assertThat(result.get(2016).doubleValue(), is(10.0));
		assertThat(result.get(2015).doubleValue(), is(20.0));
		assertThat(result.get(2014).doubleValue(), is(10.0));
		assertThat(result.get(2013).doubleValue(), is(10.0));
		assertThat(result.get(2012).doubleValue(), is(10.0));
		assertNull(result.get(2011));
	}
	
	@Test
	public void isServicableStrictTest() {
		assertNotNull(plantInvRepo.findOne(isServicableWithId("1")));
		assertNull(plantInvRepo.findOne(isServicableWithId("25")));
	}
	
	@Test
	public void isServicableRelaxedTest() {
		BusinessPeriod period = BusinessPeriod.of(LocalDate.of(2016, 3, 22), LocalDate.of(2016, 3, 27));
		assertNotNull(plantInvRepo.findOne(isRelaxedServicableWithId("1",period)));
	
		PlantInvItem invItem = new PlantInvItem();
		invItem.setSerialNumber("99");
		invItem.setCondition(EquipmentCondition.REPAIRABLE);
		plantInvRepo.save(invItem);
		
		MaintenanceTask task = new MaintenanceTask();
		task.setId(15L);
		task.setMaintPlanId(2L);
		task.setRentalPeriod(BusinessPeriod.of(period.getStartDate().minusWeeks(4), period.getEndDate().minusWeeks(4)));
		taskRepo.save(task);
		
		MaintenancePlan plan = maintRepo.findOne(2L);
		PlantReservation res = new PlantReservation();
		res.setMaintPlan(plan);
		resevRepo.save(res);
		
		assertNull(plantInvRepo.findOne(isServicableWithId("99"))); //Should fail
		assertNotNull(plantInvRepo.findOne(isRelaxedServicableWithId("1",period))); //Maintenance is planned, should pass
	}
}

