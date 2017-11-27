package hu.tvarga.cheaplist.business.compare.shoppinglist;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShoppingListPresenterTest extends MockitoJUnitTest {

	@Mock
	private ShoppingListManager shoppingListManager;

	@Mock
	private ShoppingListContract.View view;

	@Mock
	private ShoppingListItem item;

	@Mock
	private ShoppingListItemHolder holder;

	@Mock
	private RecyclerView.Adapter<ShoppingListItemHolder> adapter;

	private ShoppingListPresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new ShoppingListPresenter(shoppingListManager);
	}

	@Test
	public void onResume() throws Exception {
		presenter.onResume(view);

		assertEquals(presenter.view, view);
	}

	@Test
	public void onPause() throws Exception {
		presenter.view = view;

		presenter.onPause();

		assertThat(presenter.view, instanceOf(ShoppingListBaseViewStub.class));
	}

	@Test
	public void removeFromList() throws Exception {
		presenter.removeFromList(item);

		verify(shoppingListManager).removeFromList(item);
	}

	@Test
	public void addToList() throws Exception {
		presenter.addToList(item);

		verify(shoppingListManager).addToList(item);
	}

	@Test
	public void setAdapter() throws Exception {
		presenter.setAdapter(adapter);

		verify(shoppingListManager).setAdapter(adapter);
	}

	@Test
	public void adapterGetItemCount() throws Exception {
		when(shoppingListManager.getItems()).thenReturn(Collections.singletonList(item));

		assertThat(presenter.adapterGetItemCount(), is(1));
	}

	@Test
	public void adapterOnBindViewHolder() throws Exception {
		presenter.view = view;
		View.OnClickListener onClickListener = mock(View.OnClickListener.class);
		when(view.getOnListItemOnClickListener(any(ShoppingListItem.class),
				any(ShoppingListItemHolder.class))).thenReturn(onClickListener);
		when(shoppingListManager.getItems()).thenReturn(Collections.singletonList(item));

		presenter.adapterOnBindViewHolder(holder, 0);

		verify(view).getOnListItemOnClickListener(item, holder);
		verify(holder).bind(item, shoppingListManager, onClickListener);
	}
}