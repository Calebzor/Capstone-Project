package hu.tvarga.capstone.cheaplist.business.compare;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import timber.log.Timber;

public class CategoryValueEventListener implements ValueEventListener {

	private final List<ItemCategory> categories;
	private final CategoriesDBCallback callback;

	public interface CategoriesDBCallback {

		void success();
	}

	public CategoryValueEventListener(List<ItemCategory> categories,
			CategoriesDBCallback callback) {
		this.categories = categories;
		this.callback = callback;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		Iterable<DataSnapshot> children = dataSnapshot.getChildren();
		categories.clear();
		for (DataSnapshot child : children) {
			ItemCategory item = child.getValue(ItemCategory.class);
			categories.add(item);
		}
		callback.success();
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		Timber.d("getCategoriesFromDB#onCancelled %s", databaseError);
	}
}
