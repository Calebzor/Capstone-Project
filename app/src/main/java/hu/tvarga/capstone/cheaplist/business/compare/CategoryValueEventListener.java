package hu.tvarga.capstone.cheaplist.business.compare;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CategoryValueEventListener implements ValueEventListener {

	private final List<String> categories;
	private final CategoriesDBCallback callback;

	public interface CategoriesDBCallback {

		void success();
	}

	public CategoryValueEventListener(List<String> categories, CategoriesDBCallback callback) {
		this.categories = categories;
		this.callback = callback;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		GenericTypeIndicator<Map<String, String>> genericTypeIndicator =
				new GenericTypeIndicator<Map<String, String>>() {};
		Map<String, String> categoriesFromDB = dataSnapshot.getValue(genericTypeIndicator);
		if (categoriesFromDB != null) {
			categories.clear();
			for (Map.Entry<String, String> pair : categoriesFromDB.entrySet()) {
				categories.add(pair.getValue());
			}
			Collections.sort(categories);
			callback.success();
		}
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		Timber.d("getCategoriesFromDB#onCancelled %s", databaseError);
	}
}
