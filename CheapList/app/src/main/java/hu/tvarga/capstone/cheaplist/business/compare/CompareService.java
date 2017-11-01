package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.BuildConfig;
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.capstone.cheaplist.utility.broadcast.Broadcast;
import timber.log.Timber;

@ApplicationScope
public class CompareService {

	public static final String CATEGORIES_BROADCAST = "CATEGORIES_BROADCAST";
	private final Broadcast broadcast;
	private DatabaseReference databaseReferencePublic;
	private List<MerchantCategoryListItem> startItems = new LinkedList<>();
	private List<MerchantCategoryListItem> endItems = new LinkedList<>();
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> startAdapter;
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> endAdapter;
	private DatabaseReference startMerchantItemsDBRef;
	private DatabaseReference endMerchantItemsDBRef;

	private ArrayList<String> categories = new ArrayList<>();
	private Map<String, Merchant> merchantMap;
	private Merchant startMerchant;
	private Merchant endMerchant;
	private String category = "ALCOHOL";

	@Inject
	public CompareService(Broadcast broadcast) {
		this.broadcast = broadcast;
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		databaseReferencePublic = firebaseDatabase.getReference().child("publicReadable");

		FirebaseRemoteConfigSettings configSettings =
				new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(
						BuildConfig.DEBUG).build();
		firebaseRemoteConfig.setConfigSettings(configSettings);

		getCategoriesFromDB();
		getMerchantsFromDB();
	}

	private void getCategoriesFromDB() {
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
							gotCategoriesFromDB();
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("addAuthStateListener#onCancelled %s", databaseError);
					}
				});
	}

	private void getMerchantsFromDB() {
		databaseReferencePublic.child("merchants").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<HashMap<String, Merchant>> genericTypeIndicator =
								new GenericTypeIndicator<HashMap<String, Merchant>>() {};
						merchantMap = dataSnapshot.getValue(genericTypeIndicator);
						gotMerchantsFromDB();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("getMerchantsFromDB#onCancelled %s", databaseError);
					}
				});
	}

	private void gotMerchantsFromDB() {
		Timber.d("gotMerchantsFromDB");
		getMerchantCategoryData();
	}

	private void gotCategoriesFromDB() {
		Timber.d("gotCategoriesFromDB");
		getMerchantCategoryData();
	}

	private boolean isMerchantAndCategoryAvailable() {
		return merchantMap != null && !merchantMap.isEmpty() && !categories.isEmpty();
	}

	private void getMerchantCategoryData() {
		if (isMerchantAndCategoryAvailable()) {
			CategoriesBroadcastObject categoriesBroadcastObject = new CategoriesBroadcastObject(
					categories);
			broadcast.sendObject(categoriesBroadcastObject);
			getData();
		}
	}

	private void getData() {
		parseMerchantsAndSetCategoryDBRef();
		getStartItemsFromDB();
		getEndItemsFromDB();
	}

	private DatabaseReference getDBRefForMerchantCategoryList(Map.Entry<String, Merchant> entry) {
		String key = entry.getKey() + category;
		return FirebaseDatabase.getInstance().getReference().child("publicReadable").child(
				"merchantCategoryListItems").child(key);
	}

	private void parseMerchantsAndSetCategoryDBRef() {
		boolean startSet = false;
		for (Map.Entry<String, Merchant> entry : merchantMap.entrySet()) {
			if (!startSet) {
				startMerchantItemsDBRef = getDBRefForMerchantCategoryList(entry);
				startMerchant = entry.getValue();
				startSet = true;
			}
			else {
				endMerchantItemsDBRef = getDBRefForMerchantCategoryList(entry);
				endMerchant = entry.getValue();
			}
		}
	}

	private void getStartItemsFromDB() {
		Query query = startMerchantItemsDBRef.orderByChild("name");
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				GenericTypeIndicator<HashMap<String, MerchantCategoryListItem>>
						genericTypeIndicator =
						new GenericTypeIndicator<HashMap<String, MerchantCategoryListItem>>() {};
				HashMap<String, MerchantCategoryListItem> value = dataSnapshot.getValue(
						genericTypeIndicator);
				if (value != null && !value.isEmpty()) {
					startItems.clear();
					startItems.addAll(value.values());
					if (startAdapter != null) {
						startAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Timber.d("getStartItemsFromDB#onCancelled %s", databaseError);
			}
		});
	}

	private void getEndItemsFromDB() {
		Query query = endMerchantItemsDBRef.orderByChild("name");
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				GenericTypeIndicator<HashMap<String, MerchantCategoryListItem>>
						genericTypeIndicator =
						new GenericTypeIndicator<HashMap<String, MerchantCategoryListItem>>() {};
				HashMap<String, MerchantCategoryListItem> value = dataSnapshot.getValue(
						genericTypeIndicator);
				if (value != null && !value.isEmpty()) {
					endItems.clear();
					endItems.addAll(value.values());
					if (endAdapter != null) {
						endAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Timber.d("getEndItemsFromDB#onCancelled %s", databaseError);
			}
		});
	}

	List<MerchantCategoryListItem> getStartItems() {
		return startItems;
	}

	List<MerchantCategoryListItem> getEndItems() {
		return endItems;
	}

	void setStartAdapter(RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter) {
		startAdapter = adapter;
	}

	void setEndAdapter(RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter) {
		endAdapter = adapter;
	}

	Merchant getStartMerchant() {
		return startMerchant;
	}

	Merchant getEndMerchant() {
		return endMerchant;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategory(String category) {
		this.category = category;
		getData();
	}
}
