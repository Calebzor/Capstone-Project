package hu.tvarga.cheaplist.business.compare;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import hu.tvarga.cheaplist.BaseMockitoJUnitTest;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;

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
		presenter.categories = new ArrayList<>();

		presenter.onResume(view);

		assertEquals(presenter.view, view);
		checkEventRegister();
	}

	@Test
	public void onPause() throws Exception {
		presenter.onPause();

		checkEventUnregister();
	}
}