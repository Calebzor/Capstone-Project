package hu.tvarga.capstone.cheaplist.business.compare;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.business.compare.CompareService.ITEM_CATEGORIES;

public class CategoryValueEventListener implements EventListener<DocumentSnapshot> {

	private final List<ItemCategory> categories;
	private final CategoriesDBCallback callback;

	@Override
	public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {
		if (e != null) {
			Timber.e("getCategoriesFromDB#onCancelled %s", e);
			return;
		}
		Object snapshotObject = dataSnapshot.get(ITEM_CATEGORIES);
		if (snapshotObject instanceof List) {
			categories.clear();
			List<String> categoriesFromDB = (List<String>) snapshotObject;
			for (String categoryAsString : categoriesFromDB) {
				categories.add(ItemCategory.valueOf(categoryAsString));
			}
		}
		callback.success();
	}

	public interface CategoriesDBCallback {

		void success();
	}

	public CategoryValueEventListener(List<ItemCategory> categories,
			CategoriesDBCallback callback) {
		this.categories = categories;
		this.callback = callback;
	}

}
