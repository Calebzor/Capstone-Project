package hu.tvarga.capstone.cheaplist.business.compare;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.dao.Merchant;
import timber.log.Timber;

public class MerchantValueEventListener implements EventListener<QuerySnapshot> {

	private final Map<String, Merchant> merchantMap;
	private final MerchantsDBCallback callback;

	@Override
	public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
		if (e != null) {
			Timber.e("getMerchantsFromDB#onCancelled %s", e);
			return;
		}
		List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
		HashMap<String, Merchant> value = new HashMap<>();
		for (DocumentSnapshot document : documents) {
			Merchant merchant = document.toObject(Merchant.class);
			value.put(document.getId(), merchant);
		}
		if (!value.isEmpty()) {
			merchantMap.clear();
			merchantMap.putAll(value);
		}
		callback.success();
	}

	public interface MerchantsDBCallback {

		void success();
	}

	public MerchantValueEventListener(Map<String, Merchant> merchantMap,
			MerchantsDBCallback callback) {
		this.merchantMap = merchantMap;
		this.callback = callback;
	}

}

