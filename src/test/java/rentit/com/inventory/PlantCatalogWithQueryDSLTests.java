package rentit.com.inventory;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static rentit.com.maintenance.domain.model.MaintenanceSpecifications.isWithinPastYears;
import static rentit.com.sales.domain.model.InventorySpecifications.isAvailableFor;
import static rentit.com.sales.domain.model.InventorySpecifications.isRelaxedServicableWithId;
import static rentit.com.sales.domain.model.InventorySpecifications.isServicableWithId;
import static rentit.com.sales.domain.model.InventorySpecifications.nameContains;

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

import rentit.com.RentitApplication;
import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.model.PlantInvItem;
import rentit.com.inventory.domain.model.PlantInvItem.EquipmentCondition;
import rentit.com.inventory.domain.model.PlantReservation;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;
import rentit.com.inventory.domain.repository.PlantInvItemRepository;
import rentit.com.inventory.domain.repository.PlantReservationRepository;
import rentit.com.maintenance.domain.model.MaintenancePlan;
import rentit.com.maintenance.domain.model.MaintenanceTask;
import rentit.com.maintenance.domain.repository.MaintenancePlanRepository;
import rentit.com.maintenance.domain.repository.MaintenanceTaskRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@Sql(scripts="/plants-dataset.sql")
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
		assertThat(entries.size(), equalTo(1));
	}
	
	@Test
	public void correctiveRepairCountTest() {
		List<MaintenancePlan> entries = Lists.newArrayList(maintRepo.findAll(isWithinPastYears(5)));
		Map<Integer,Integer> result = entries.stream().collect(Collectors.toMap(MaintenancePlan::getYearOfAction, v->v.getTasks().size()));
		assertThat(result.get(2016), equalTo(1));
		assertThat(result.get(2015), equalTo(2));
		assertThat(result.get(2014), equalTo(1));
		assertThat(result.get(2013), equalTo(1));
		assertThat(result.get(2012), equalTo(1));
		assertNull(result.get(2011));
	}
	
	@Test
	public void correctiveRepairSumTest() {
		List<MaintenancePlan> entries = Lists.newArrayList(maintRepo.findAll(isWithinPastYears(5)));
		Map<Integer,BigDecimal> result = entries.stream().collect(Collectors.toMap(MaintenancePlan::getYearOfAction, 
				v->v.getTasks().stream().map(MaintenanceTask::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
		assertThat(result.get(2016).doubleValue(), equalTo(10.0));
		assertThat(result.get(2015).doubleValue(), equalTo(20.0));
		assertThat(result.get(2014).doubleValue(), equalTo(10.0));
		assertThat(result.get(2013).doubleValue(), equalTo(10.0));
		assertThat(result.get(2012).doubleValue(), equalTo(10.0));
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
		task.setSchedule(BusinessPeriod.of(period.getStartDate().minusWeeks(4), period.getEndDate().minusWeeks(4)));
		taskRepo.save(task);
		
		MaintenancePlan plan = maintRepo.findOne(2L);
		PlantReservation res = new PlantReservation();
		res.setMaintPlanId(plan.getId());
		resevRepo.save(res);
		
		assertNull(plantInvRepo.findOne(isServicableWithId("99"))); //Should fail
		assertNotNull(plantInvRepo.findOne(isRelaxedServicableWithId("1",period))); //Maintenance is planned, should pass
	}
}

