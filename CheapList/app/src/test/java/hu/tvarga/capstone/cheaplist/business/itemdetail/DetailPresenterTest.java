package hu.tvarga.capstone.cheaplist.business.itemdetail;

import org.junit.Test;
import org.mockito.Mock;

import hu.tvarga.capstone.cheaplist.BaseMockitoJUnitTest;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

public class DetailPresenterTest extends BaseMockitoJUnitTest {

	@Mock
	private ShoppingListManager shoppingListManager;

	@Mock
	private DetailPresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new DetailPresenter(shoppingListManager);
	}

	@Test
	public void getManufacturerInformation() throws Exception {
		ManufacturerInformation manufacturerInformation = new ManufacturerInformation();
		manufacturerInformation.address = "address";
		manufacturerInformation.contact = "contact";
		manufacturerInformation.supplier = "supplier";

		assertThat(presenter.getManufacturerInformation(manufacturerInformation),
				containsString(manufacturerInformation.address));
		assertThat(presenter.getManufacturerInformation(manufacturerInformation),
				containsString(manufacturerInformation.contact));
		assertThat(presenter.getManufacturerInformation(manufacturerInformation),
				containsString(manufacturerInformation.supplier));
	}

	@Test
	public void getManufacturerInformation_emptyString() throws Exception {
		ManufacturerInformation manufacturerInformation = new ManufacturerInformation();

		assertThat(presenter.getManufacturerInformation(manufacturerInformation), is(""));
	}
}