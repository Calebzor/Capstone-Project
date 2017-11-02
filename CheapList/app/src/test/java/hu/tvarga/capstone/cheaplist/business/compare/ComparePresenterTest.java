package hu.tvarga.capstone.cheaplist.business.compare;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import hu.tvarga.capstone.cheaplist.BaseMockitoJUnitTest;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectListener;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectReceiver;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectReceiverFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComparePresenterTest extends BaseMockitoJUnitTest {

	@Mock
	private ObjectReceiver<CategoriesBroadcastObject> categoriesBroadcastObjectObjectReceiver;

	@Mock
	private ObjectReceiverFactory objectReceiverFactory;

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
		//noinspection unchecked
		when(objectReceiverFactory
				.get(any(ObjectListener.class), eq(CategoriesBroadcastObject.class))).thenReturn(
				categoriesBroadcastObjectObjectReceiver);
		presenter = new ComparePresenter(shoppingListManager, compareService,
				objectReceiverFactory);
	}

	@Test
	public void onResume() throws Exception {
		ArrayList<String> list = new ArrayList<>();
		presenter.categories = list;

		presenter.onResume(view);

		assertEquals(presenter.view, view);
		verify(categoriesBroadcastObjectObjectReceiver).register();
		verify(view).notifyGotMerchantCategoryData(list);
	}

	@Test
	public void onPause() throws Exception {
		presenter.onPause();

		verify(categoriesBroadcastObjectObjectReceiver).unregister();
	}
}