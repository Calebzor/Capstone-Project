package hu.tvarga.cheaplist.business.compare;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import hu.tvarga.cheaplist.BaseMockitoJUnitTest;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.dao.ItemCategory;

import static org.mockito.Mockito.verify;

public class ComparePresenterTest extends BaseMockitoJUnitTest {

	@Mock
	private ShoppingListManager shoppingListManager;

	@Mock
	private CompareService compareService;

	@Mock
	private CompareContract.View view;

	private ComparePresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new ComparePresenter(shoppingListManager, compareService, event);
	}

	@Test
	public void onResume() throws Exception {
		ArrayList<ItemCategory> list = new ArrayList<>();
		presenter.categories = list;

		presenter.onResume(view);

		assertEquals(presenter.view, view);
		checkEventRegister();
		verify(view).notifyGotMerchantCategoryData(list);
	}

	@Test
	public void onPause() throws Exception {
		presenter.onPause();

		checkEventUnregister();
	}
}