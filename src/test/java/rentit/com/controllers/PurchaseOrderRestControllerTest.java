package rentit.com.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import rentit.com.common.application.dto.BusinessPeriodDTO;
import rentit.com.inventory.application.dto.PlantInvEntryDTO;
import rentit.com.sales.application.dto.PurchaseOrderDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@WebAppConfiguration
@Sql({"/schema.sql", "/plants-dataset.sql"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PurchaseOrderRestControllerTest {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired @Qualifier("_halObjectMapper")
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAllPlants() throws Exception {
		MvcResult result = mockMvc.perform(get(getDefaultGetPlantsUri()))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();

		List<PlantInvEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInvEntryDTO>>() { });

		assertThat(plants.size(), equalTo(6));
		
		PurchaseOrderDTO order = createPO(plants.get(2),LocalDate.now(),LocalDate.now());
		
		//Test successfully creating new PO
		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
	}
	
	private static String formatDate(LocalDate date){
		return date.format(formatter);
	}
	
	@Test	
	public void testModifyExistingPO() throws Exception{
		final LocalDate now = LocalDate.now();
		
		//Create PO
		PurchaseOrderDTO partialOrder = createPO(getPlantWithId(1L), now, now.plusDays(3));
		
		MvcResult poResult = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(partialOrder)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated()).andReturn();
		PurchaseOrderDTO createdPo = mapper.readValue(poResult.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
		
		//Reject PO
		MvcResult rejectResult =  mockMvc.perform(delete(createdPo.getXlink("reject").getHref()))
			.andExpect(status().isOk()).andReturn();
		PurchaseOrderDTO rejectedPo = mapper.readValue(rejectResult.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
		
		//Modify existing PO, set startDate and endDate to some available period
		rejectedPo.setRentalPeriod(BusinessPeriodDTO.of(now.plusDays(10), now.plusDays(15)));
		mockMvc.perform(put(rejectedPo.getXlink("modify").getHref()).content(mapper.writeValueAsString(rejectedPo)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	private PlantInvEntryDTO getPlantWithId(long id) throws Exception{
		MvcResult result = mockMvc.perform(get("/api/inventory/plants/"+id))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
		return mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PlantInvEntryDTO>() { });
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
		mockMvc.perform(get("/api/sales/orders/55"))
				.andExpect(status().isNotFound())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
	}
	
	@Test
	public void testGetAllPOs() throws Exception{	
		PurchaseOrderDTO order = createPO(getPlantWithId(14),LocalDate.now(),LocalDate.now().plusDays(1));
		
		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
		
		//Retrieve all PO's
		MvcResult result1 = mockMvc.perform(get("/api/sales/orders"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();
		List<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<List<PurchaseOrderDTO>>() { });
		assertThat(orders.size(), equalTo(2));
	}
	
	@Test
	public void testPOTotalCost() throws Exception{
		PurchaseOrderDTO order = createPO(getPlantWithId(14),LocalDate.now(),LocalDate.now().plusDays(2));
		
		MvcResult poResult =  mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		
		PurchaseOrderDTO po = mapper.readValue(poResult.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() { });
		assertTrue(po.getCost().compareTo(BigDecimal.valueOf(900))==0);
	}

	private static PurchaseOrderDTO createPO(PlantInvEntryDTO plantInvEntryDTO, LocalDate startDate, LocalDate endDate) {
		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.setPlant(plantInvEntryDTO);
		order.setRentalPeriod(BusinessPeriodDTO.of(startDate, endDate));
		return order;
	}
	
	public static String getDefaultGetPlantsUri(){
		String startDate = formatDate(LocalDate.now());
		String endDate = formatDate(LocalDate.now().plusDays(1));
		return String.format("/api/inventory/plants?name=Exc&startDate=%s&endDate=%s", startDate, endDate);
	}
	
	@Test
	public void testPurchaseOrderAcceptance() throws Exception {
		MvcResult result = mockMvc.perform( get(getDefaultGetPlantsUri())).andReturn();
		List<PlantInvEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInvEntryDTO>>() {});
		
		final LocalDate now = LocalDate.now();
		PurchaseOrderDTO order = createPO(plants.get(2), now, now);
	
		result = mockMvc.perform(post("/api/sales/orders")
	                       .content(mapper.writeValueAsString(order))
	                       .contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(header().string("Location", not(isEmptyOrNullString())))
						.andReturn();
	
		order = mapper.readValue(result.getResponse().getContentAsString(), PurchaseOrderDTO.class);
	
		assertThat(order.getXlink("accept"), is(notNullValue()));
	
		mockMvc.perform(post(order.getXlink("accept").getHref()))
	      	.andReturn();
	}
}

