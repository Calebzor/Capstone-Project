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
import java.util.HashMap;
import java.util.Iterator;
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
import hu.tvarga.capstone.cheaplist.utility.EventBusWrapper;
import hu.tvarga.capstone.cheaplist.utility.StringUtils;
import timber.log.Timber;

@ApplicationScope
public class CompareService {

	private final EventBusWrapper eventBusWrapper;
	private DatabaseReference databaseReferencePublic;
	private List<MerchantCategoryListItem> startItems = new LinkedList<>();
	private List<MerchantCategoryListItem> endItems = new LinkedList<>();
	private List<MerchantCategoryListItem> startItemsUnfiltered = new LinkedList<>();
	private List<MerchantCategoryListItem> endItemsUnfiltered = new LinkedList<>();
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> startAdapter;
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> endAdapter;
	private DatabaseReference startMerchantItemsDBRef;
	private DatabaseReference endMerchantItemsDBRef;

	private ArrayList<String> categories = new ArrayList<>();
	private Map<String, Merchant> merchantMap = new HashMap<>();
	private Merchant startMerchant;
	private Merchant endMerchant;
	private String category = "ALCOHOL";
	private String filter;

	@Inject
	CompareService(EventBusWrapper eventBusWrapper) {
		this.eventBusWrapper = eventBusWrapper;
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
				new CategoryValueEventListener(categories,
						new CategoryValueEventListener.CategoriesDBCallback() {
							@Override
							public void success() {
								gotCategoriesFromDB();
							}
						}));
	}

	private void getMerchantsFromDB() {
		databaseReferencePublic.child("merchants").addListenerForSingleValueEvent(
				new MerchantValueEventListener(merchantMap,
						new MerchantValueEventListener.MerchantsDBCallback() {
							@Override
							public void success() {
								gotMerchantsFromDB();
							}
						}));
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
			eventBusWrapper.getDefault().post(categoriesBroadcastObject);
			getData();
		}
	}

	public void getData() {
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
					startItemsUnfiltered.clear();
					startItemsUnfiltered.addAll(value.values());
					if (startAdapter != null) {
						filterStart();
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
					endItemsUnfiltered.clear();
					endItemsUnfiltered.addAll(value.values());
					if (endAdapter != null) {
						filterEnd();
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

	private void filterStart() {
		startItems.clear();
		startItems.addAll(startItemsUnfiltered);
		if (StringUtils.isEmpty(filter)) {
			startAdapter.notifyDataSetChanged();
			return;
		}
		removeItemFromStartList(startItems);
		startAdapter.notifyDataSetChanged();
	}

	private void filterEnd() {
		endItems.clear();
		endItems.addAll(endItemsUnfiltered);
		if (StringUtils.isEmpty(filter)) {
			endAdapter.notifyDataSetChanged();
			return;
		}
		removeItemFromStartList(endItems);
		endAdapter.notifyDataSetChanged();
	}

	private void removeItemFromStartList(List<MerchantCategoryListItem> items) {
		for (Iterator<MerchantCategoryListItem> iterator = items.iterator(); iterator.hasNext(); ) {
			MerchantCategoryListItem item = iterator.next();
			if (!item.name.toLowerCase().contains(filter.toLowerCase())) {
				iterator.remove();
			}
		}
	}

	void setFilter(String filter) {
		this.filter = filter;
		filterStart();
		filterEnd();
	}

}
