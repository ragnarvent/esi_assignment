package rentit.com;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDateInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import rentit.com.inventory.domain.model.PlantInvEntry;
import rentit.com.inventory.domain.model.PlantInvItem;
import rentit.com.inventory.domain.model.PlantInvItem.EquipmentCondition;
import rentit.com.inventory.domain.repository.PlantInvEntryRepository;
import rentit.com.inventory.domain.repository.PlantInvItemRepository;
import rentit.com.inventory.domain.repository.PlantReservationRepository;
import rentit.com.sales.domain.repository.PurchaseOrderRepository;

@ContextConfiguration(classes = { RentitApplication.class,
		Jsr310JpaConverters.class }, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
public class CreationOfPurchaseOrderSteps {

	@Autowired
	private WebApplicationContext wac;

	private WebClient customerBrowser;
	private HtmlPage customerPage;

	@Autowired
	private PlantInvEntryRepository plantEntryRepo;
	
	@Autowired
	private PlantInvItemRepository plantItemRepo;
	
	@Autowired
	private PurchaseOrderRepository poRepo;

	@Autowired
	private PlantReservationRepository reservationRepo;
	
	@Before
	public void setUp() {
		customerBrowser = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
	}

	@After
	public void tearOff() {
		poRepo.deleteAll();
		reservationRepo.deleteAll();
		plantItemRepo.deleteAll();
		plantEntryRepo.deleteAll();
	}

	@Given("^the following plants are currently available for rental$")
	public void the_following_plants_are_currently_available_for_rental(List<PlantInvEntry> plants)
			throws Throwable {
		plantEntryRepo.save(plants);
		plantItemRepo.save(plants.stream().map(p->PlantInvItem.of(String.valueOf(UUID.randomUUID().toString()),EquipmentCondition.SERVICEABLE,p)).collect(Collectors.toList()));
	}

	@Given("^a customer is in the \"([^\"]*)\" web page$")
	public void a_customer_is_in_the_web_page(String pageTitle) throws Throwable {
		customerPage = customerBrowser.getPage("http://localhost/dashboard/catalog/form");
	}
	
	@Given("^no purchase order exists in the system$")
	public void no_purchase_order_exists_in_the_system() throws Throwable {
	}

	@When("^the customer queries the plant catalog for an \"([^\"]*)\" available from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void the_customer_queries_the_plant_catalog_for_an_available_from_to(String plantName, String startDate,
			String endDate) throws Throwable {
		// The following elements are selected by their identifier
		HtmlTextInput nameInput = (HtmlTextInput) customerPage.getElementById("name");
		HtmlDateInput startDateInput = (HtmlDateInput) customerPage.getElementById("rental-start-date");
		HtmlDateInput endDateInput = (HtmlDateInput) customerPage.getElementById("rental-end-date");
		HtmlButton submit = (HtmlButton) customerPage.getElementById("submit-button");

		nameInput.setValueAttribute(plantName);
		startDateInput.setValueAttribute(startDate);
		endDateInput.setValueAttribute(endDate);

		customerPage = submit.click();
	}

	@Then("^(\\d+) plants are shown$")
	public void plants_are_shown(int numberOfPlants) throws Throwable {
		List<?> rows = customerPage.getByXPath("//tr[contains(@class, 'table-row')]");
		assertThat(rows.size(), equalTo(numberOfPlants));
	}

	@When("^the customer selects a \"([^\"]*)\"$")
	public void the_customer_selects_a(String plantDescription) throws Throwable {
		List<?> buttons = customerPage.getByXPath(String.format("//tr[./td = '%s']//button", plantDescription));
		assertThat(buttons.size(), equalTo(1));
		HtmlButton createPo = (HtmlButton)buttons.get(0);
		customerPage = createPo.click();
	}

	@Then("^a purchase order should be created with a total price of (\\d+\\.\\d+)$")
	public void a_purchase_order_should_be_created_with_a_total_price_of(BigDecimal total) throws Throwable {
		List<?> cells = customerPage.getByXPath("//tr[contains(.,'Cost')]//td[last()]");
		assertThat(cells.size(), equalTo(1));
		
		HtmlTableDataCell costCell = (HtmlTableDataCell)cells.get(0);
		BigDecimal val = new BigDecimal(costCell.getTextContent());
		
		assertTrue(total.compareTo(val) == 0);
	}
}
