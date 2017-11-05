package hu.tvarga.capstone.cheaplist.business.compare;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.dao.Merchant;
import timber.log.Timber;

public class MerchantValueEventListener implements ValueEventListener {

	private final Map<String, Merchant> merchantMap;
	private final MerchantsDBCallback callback;

	public interface MerchantsDBCallback {

		void success();
	}

	public MerchantValueEventListener(Map<String, Merchant> merchantMap,
			MerchantsDBCallback callback) {
		this.merchantMap = merchantMap;
		this.callback = callback;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		GenericTypeIndicator<HashMap<String, Merchant>> genericTypeIndicator =
				new GenericTypeIndicator<HashMap<String, Merchant>>() {};
		HashMap<String, Merchant> value = dataSnapshot.getValue(genericTypeIndicator);
		if (value != null) {
			merchantMap.clear();
			merchantMap.putAll(value);
		}
		callback.success();
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		Timber.d("getMerchantsFromDB#onCancelled %s", databaseError);
	}
}

