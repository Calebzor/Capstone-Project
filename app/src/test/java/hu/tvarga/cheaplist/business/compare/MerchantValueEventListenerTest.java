package hu.tvarga.cheaplist.business.compare;

import com.google.firebase.firestore.FirebaseFirestoreException;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.dao.Merchant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MerchantValueEventListenerTest extends MockitoJUnitTest {

	@Mock
	private Map<String, Merchant> merchantMap;

	@Mock
	private MerchantValueEventListener.MerchantsDBCallback callback;

	private MerchantValueEventListener merchantValueEventListener;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		merchantValueEventListener = new MerchantValueEventListener(merchantMap, callback);
	}

	@Test
	public void onEvent_WithException() throws Exception {
		merchantValueEventListener.onEvent(null, mock(FirebaseFirestoreException.class));

		verify(callback, never()).success();
	}
}