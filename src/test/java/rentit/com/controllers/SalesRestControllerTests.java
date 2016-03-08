package rentit.com.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rentit.com.RentitApplication;
import rentit.com.inventory.domain.PlantInvEntryRepository;
import rentit.com.sales.domain.PurchaseOrder.POStatus;
import rentit.com.web.dto.BusinessPeriodDTO;
import rentit.com.web.dto.PlantInvEntryDTO;
import rentit.com.web.dto.PurchaseOrderDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@WebAppConfiguration
@Sql({"/schema.sql", "/plants-dataset.sql"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SalesRestControllerTests {
	
	@Autowired
	PlantInvEntryRepository repo;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired
	ObjectMapper mapper;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAllPlants() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/sales/plants?name=Exc&startDate=2016-03-14&endDate=2016-03-25"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();

		List<PlantInvEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInvEntryDTO>>() { });

		assertThat(plants.size(), equalTo(6));
		
		PurchaseOrderDTO order = createPO(plants.get(2).getEntryId(),LocalDate.now(),LocalDate.now());
		
		//Test successfully creating new PO
		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		
	}
	
	@Test	
	public void testModifyExistingPO() throws Exception{
		
		PurchaseOrderDTO order = createPO(1L,LocalDate.of(2016, 3, 23),LocalDate.of(2016, 3, 25));
		
		//Try to create new PO
		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());  //Will fail because plant is already reserved for that period, PO will be set as REJECTED
		
		//Get all POs
		MvcResult result = mockMvc.perform(get("/api/sales/allorders"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
		List<PurchaseOrderDTO> orders = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PurchaseOrderDTO>>() { });
		assertThat(orders.size(), equalTo(2));
		
		//Find the rejected one
		PurchaseOrderDTO rejectedPO = orders.stream().filter(o->o.getStatus() == POStatus.REJECTED).findFirst().get();
		assertNotNull(rejectedPO);
		
		//Modify existing PO
		rejectedPO.setRentalPeriod(BusinessPeriodDTO.of("2016-03-26", "2016-03-28"));
		mockMvc.perform(post("/api/sales/modifyorder").content(mapper.writeValueAsString(rejectedPO)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void testFetchSinglePOSuccess() throws Exception{
		mockMvc.perform(get("/api/sales//orders/100"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
	}
	
	@Test
	public void testFetchSinglePOFail() throws Exception{
		mockMvc.perform(get("/api/sales//orders/55"))
				.andExpect(status().isNotFound())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
	}
	
	@Test
	public void testGetAllPOs() throws Exception{	
		
		PurchaseOrderDTO order = createPO(14,LocalDate.now(),LocalDate.now().plusDays(1));
		
		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
		
		//Retrieve all PO's
		MvcResult result1 = mockMvc.perform(get("/api/sales/allorders"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
		List<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<List<PurchaseOrderDTO>>() { });
		assertThat(orders.size(), equalTo(2));
	}
	
	@Test
	public void testPOTotalCost() throws Exception{
		
		PurchaseOrderDTO order = createPO(14,LocalDate.now(),LocalDate.now().plusDays(2));
		
		MvcResult poResult =  mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		
		PurchaseOrderDTO po = mapper.readValue(poResult.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() { });
		assertTrue(po.getCost().compareTo(BigDecimal.valueOf(900))==0);
	}

	private static PurchaseOrderDTO createPO(long plantID, LocalDate startDate, LocalDate endDate) {
		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.setPlantId(plantID);
		order.setRentalPeriod(BusinessPeriodDTO.of(startDate.toString(), endDate.toString()));
		return order;
	}
}

