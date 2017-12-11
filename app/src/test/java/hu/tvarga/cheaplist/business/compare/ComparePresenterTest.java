package hu.tvarga.cheaplist.business.compare;

import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.dao.ItemCategory;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComparePresenterTest extends MockitoJUnitTest {

    private static final String QUERY = "query";

    @Mock
    private ShoppingListManager shoppingListManager;

	@Mock
	private CompareService compareService;

	@Mock
	private CompareContract.View view;

	@Mock
	private SearchView searchView;

	@Mock
	private MenuItem menuItem;

	private ComparePresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new ComparePresenter(shoppingListManager, compareService, event);
	}

	@Test
	public void onResume() throws Exception {
		presenter.onResume(view);

		verify(compareService).getCategories();
		assertEquals(presenter.view, view);
		checkEventRegister();
	}

	@Test
	public void onPause() throws Exception {
		presenter.searchView = searchView;

		presenter.onPause();

		assertThat(presenter.view, instanceOf(CompareTabsViewStub.class));
		checkEventUnregister();
	}

	@Test
	public void setOnQueryTextListener() throws Exception {
		String filter = "filter";
		when(compareService.getFilter()).thenReturn(filter);

		presenter.setOnQueryTextListener(searchView, menuItem);

		verify(menuItem).expandActionView();
		verify(searchView).setQuery(filter, false);
		verify(searchView).setOnQueryTextListener(any(SearchView.OnQueryTextListener.class));
	}

	@Test
	public void handleCategoriesBroadcastObject() throws Exception {
		assertNull(presenter.categories);
		CategoriesBroadcastObject object = new CategoriesBroadcastObject(
				new ArrayList<ItemCategory>());

		presenter.handleCategoriesBroadcastObject(object);

		assertNotNull(presenter.categories);
	}

    @Test
    public void getOnQueryTextListener_onQueryTextSubmit() throws Exception {
        SearchView.OnQueryTextListener onQueryTextListener = presenter.getOnQueryTextListener();

        assertFalse(onQueryTextListener.onQueryTextSubmit(QUERY));
    }

    @Test
    public void getOnQueryTextListener_onQueryTextChange() throws Exception {
        SearchView.OnQueryTextListener onQueryTextListener = presenter.getOnQueryTextListener();

        assertFalse(onQueryTextListener.onQueryTextChange(QUERY));
        verify(compareService).setFilter(QUERY);
    }
}