package hu.tvarga.cheaplist.jobservices;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.tvarga.cheaplist.business.compare.CategoryValueEventListener;
import hu.tvarga.cheaplist.business.compare.MerchantValueEventListener;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.Merchant;
import timber.log.Timber;

import static hu.tvarga.cheaplist.business.compare.CompareService.ITEM_CATEGORIES;
import static hu.tvarga.cheaplist.business.compare.CompareService.MERCHANTS;

public class CategoriesJobService extends JobService {

	private List<ItemCategory> categories = new ArrayList<>();
	private HashMap<String, Merchant> merchantMap = new HashMap<>();
	FirebaseFirestore db;

	@Override
	public boolean onStartJob(JobParameters job) {
		db = FirebaseFirestore.getInstance();
		getCategories();
		getMerchants();

		return true;
	}

	@Override
	public boolean onStopJob(JobParameters job) {
		return true;
	}

	private void gotMerchants() {
		Timber.d("gotMerchants");
		getMerchantCategoryData();
	}

	private void gotCategories() {
		Timber.d("gotCategories");
		getMerchantCategoryData();
	}

	private void getMerchantCategoryData() {
		if (isMerchantAndCategoryAvailable()) {
			Timber.d("getting merchant categories");
			for (Map.Entry<String, Merchant> merchant : merchantMap.entrySet()) {
				for (ItemCategory category : categories) {
					final String key = merchant.getKey() + category;
					DocumentReference ref = FirebaseFirestore.getInstance().collection(
							"merchantItems").document(key);
					ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(DocumentSnapshot documentSnapshot,
								FirebaseFirestoreException e) {
							if (e != null) {
								Timber.d("Job failed to get data for key: %s, error: %s", key, e);
								return;
							}
							Timber.d("Job got data for key: %s", key);

						}
					});
				}
			}
		}
	}

	private boolean isMerchantAndCategoryAvailable() {
		return merchantMap != null && !merchantMap.isEmpty() && !categories.isEmpty();
	}

	private void getMerchants() {
		db.collection(MERCHANTS).addSnapshotListener(new MerchantValueEventListener(merchantMap,
				new MerchantValueEventListener.MerchantsDBCallback() {
					@Override
					public void success() {
						gotMerchants();
					}
				}));
	}

	private void getCategories() {
		db.collection(ITEM_CATEGORIES).document(ITEM_CATEGORIES).addSnapshotListener(
				new CategoryValueEventListener(categories,
						new CategoryValueEventListener.CategoriesDBCallback() {
							@Override
							public void success() {
								gotCategories();
							}
						}));
	}
}
