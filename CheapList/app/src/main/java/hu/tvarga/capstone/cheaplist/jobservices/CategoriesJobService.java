package hu.tvarga.capstone.cheaplist.jobservices;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.dao.Merchant;
import timber.log.Timber;

public class CategoriesJobService extends JobService {

	private List<String> categories = new ArrayList<>();
	private HashMap<String, Merchant> merchantMap;
	FirebaseDatabase firebaseDatabase;
	DatabaseReference databaseReferencePublic;

	@Override
	public boolean onStartJob(JobParameters job) {
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReferencePublic = firebaseDatabase.getReference().child("publicReadable");
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
				for (String category : categories) {
					final String key = merchant.getKey() + category;
					DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(
							"publicReadable").child("merchantCategoryListItems").child(key);
					ref.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							Timber.d("Job got data for key: %s", key);
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							Timber.d("Job failed to get data for key: %s, error: %s", key,
									databaseError);
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
		databaseReferencePublic.child("merchants").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<HashMap<String, Merchant>> genericTypeIndicator =
								new GenericTypeIndicator<HashMap<String, Merchant>>() {};
						merchantMap = dataSnapshot.getValue(genericTypeIndicator);
						gotMerchants();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("getMerchants#onCancelled %s", databaseError);
					}
				});
	}

	private void getCategories() {
		databaseReferencePublic.child("itemCategories").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<Map<String, String>> genericTypeIndicator =
								new GenericTypeIndicator<Map<String, String>>() {};
						Map<String, String> categoriesFromDB = dataSnapshot.getValue(
								genericTypeIndicator);
						if (categoriesFromDB != null) {
							categories.clear();
							for (Map.Entry<String, String> pair : categoriesFromDB.entrySet()) {
								categories.add(pair.getValue());
							}
							Collections.sort(categories);
							gotCategories();
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("addAuthStateListener#onCancelled %s", databaseError);
					}
				});
	}
}
