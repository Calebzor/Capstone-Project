package hu.tvarga.cheaplist.business.compare;

import com.google.firebase.firestore.FirebaseFirestoreException;

import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.dao.ItemCategory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CategoryValueEventListenerTest extends MockitoJUnitTest {

	@Mock
	private List<ItemCategory> categories;

	@Mock
	private CategoryValueEventListener.CategoriesDBCallback callback;

	private CategoryValueEventListener categoryValueEventListener;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		categoryValueEventListener = new CategoryValueEventListener(categories, callback);
	}

	@Test
	public void onEvent_WithException() throws Exception {
		categoryValueEventListener.onEvent(null, mock(FirebaseFirestoreException.class));

		verify(callback, never()).success();
	}
}