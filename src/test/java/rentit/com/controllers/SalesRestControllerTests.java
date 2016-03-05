package rentit.com.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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
import rentit.com.web.dto.BusinessPeriodDTO;
import rentit.com.web.dto.PlantInvEntryDTO;
import rentit.com.web.dto.PurchaseOrderDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitApplication.class)
@WebAppConfiguration
@DirtiesContext
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
	@Sql({"/schema.sql", "/rentit/com/inventory/plants-dataset.sql"})
	public void testGetAllPlants() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/sales/plants?name=Exc&startDate=2016-03-14&endDate=2016-03-25"))
				.andExpect(status().isOk())
				.andExpect(header().string("Location", isEmptyOrNullString()))
				.andReturn();

		List<PlantInvEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInvEntryDTO>>() { });

		assertThat(plants.size(), equalTo(6));

		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.setPlantId(plants.get(2).getEntryId());
		order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now().toString(), LocalDate.now().toString()));

		mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
}

