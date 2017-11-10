package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.capstone.cheaplist.utility.StringUtils;
import hu.tvarga.capstone.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

@ApplicationScope
public class CompareService {

	private static final ArrayList<UserCategoryFilterListItem> DEFAULT_FILTER = new ArrayList<>();

	static {
		UserCategoryFilterListItem filterListItem = new UserCategoryFilterListItem();
		filterListItem.category = ItemCategory.ALCOHOL;
		filterListItem.checked = true;
		DEFAULT_FILTER.add(filterListItem);
	}

	public static final String ITEMS = "items";
	public static final String MERCHANT_ITEMS = "merchantItems";
	private static final String CATEGORY_KEY = "category";
	public static final String ITEM_CATEGORIES = "itemCategories";
	public static final String MERCHANTS = "merchants";
	private final Event event;
	private List<MerchantCategoryListItem> startItems = new LinkedList<>();
	private List<MerchantCategoryListItem> endItems = new LinkedList<>();
	private List<MerchantCategoryListItem> startItemsUnfiltered = new LinkedList<>();
	private List<MerchantCategoryListItem> endItemsUnfiltered = new LinkedList<>();
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> startAdapter;
	private RecyclerView.Adapter<MerchantCategoryListItemHolder> endAdapter;
	private List<Query> startMerchantItemsDBRef;
	private List<Query> endMerchantItemsDBRef;

	private ArrayList<ItemCategory> categories = new ArrayList<>();
	private Map<String, Merchant> merchantMap = new HashMap<>();
	private Merchant startMerchant;
	private Merchant endMerchant;
	private List<UserCategoryFilterListItem> categoryFilter = DEFAULT_FILTER;
	private String filter;
	private final FirebaseFirestore db;

	@Inject
	CompareService(Event event) {
		this.event = event;
		db = FirebaseFirestore.getInstance();
		FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		FirebaseRemoteConfigSettings configSettings =
				new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(
						BuildConfig.DEBUG).build();
		firebaseRemoteConfig.setConfigSettings(configSettings);

		getCategoriesFromDB();
		getMerchantsFromDB();
	}

	private void getCategoriesFromDB() {
		db.collection(ITEM_CATEGORIES).document(ITEM_CATEGORIES).addSnapshotListener(
				new CategoryValueEventListener(categories,
						new CategoryValueEventListener.CategoriesDBCallback() {
							@Override
							public void success() {
								gotCategoriesFromDB();
							}
						}));
	}

	private void getMerchantsFromDB() {
		db.collection(MERCHANTS).addSnapshotListener(new MerchantValueEventListener(merchantMap,
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
			event.post(categoriesBroadcastObject);
			getData();
		}
	}

	public void getData() {
		parseMerchantsAndSetCategoryDBRef();
		getStartItemsFromDB();
		getEndItemsFromDB();
	}

	private List<Query> getDBRefForMerchantCategoryList(Merchant merchant) {
		Query query = db.collection(MERCHANT_ITEMS).document(merchant.id).collection(ITEMS);
		List<Query> queries = new LinkedList<>();
		for (UserCategoryFilterListItem filterListItem : categoryFilter) {
			if (filterListItem.checked) {
				queries.add(query.whereEqualTo(CATEGORY_KEY, filterListItem.category.toString()));
			}
		}

		return queries;
	}

	private void parseMerchantsAndSetCategoryDBRef() {
		boolean startSet = false;
		for (Map.Entry<String, Merchant> entry : merchantMap.entrySet()) {
			if (!startSet) {
				startMerchant = entry.getValue();
				startMerchantItemsDBRef = getDBRefForMerchantCategoryList(startMerchant);
				startSet = true;
			}
			else {
				endMerchant = entry.getValue();
				endMerchantItemsDBRef = getDBRefForMerchantCategoryList(endMerchant);
			}
		}
	}

	private void getStartItemsFromDB() {
		List<Query> queries = startMerchantItemsDBRef;
		final int[] queriesToFinish = {queries.size()};
		startItemsUnfiltered.clear();
		for (Query query : queries) {
			query.addSnapshotListener(new EventListener<QuerySnapshot>() {
				@Override
				public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
					if (e != null) {
						Timber.e("getStartItemsFromDB#onCancelled %s", e);
						return;
					}
					List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
					for (DocumentSnapshot document : documents) {
						MerchantCategoryListItem item = document.toObject(
								MerchantCategoryListItem.class);
						startItemsUnfiltered.add(item);
					}
					queriesToFinish[0] = queriesToFinish[0] - 1;
					if (startAdapter != null && queriesToFinish[0] == 0) {
						filterStart();
					}
				}
			});
		}
	}

	private void getEndItemsFromDB() {
		List<Query> queries = endMerchantItemsDBRef;
		final int[] queriesToFinish = {queries.size()};
		startItemsUnfiltered.clear();
		for (Query query : queries) {
			query.addSnapshotListener(new EventListener<QuerySnapshot>() {
				@Override
				public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
					if (e != null) {
						Timber.d("getEndItemsFromDB#onCancelled %s", e);
						return;
					}
					List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
					for (DocumentSnapshot document : documents) {
						MerchantCategoryListItem item = document.toObject(
								MerchantCategoryListItem.class);
						endItemsUnfiltered.add(item);
					}
					queriesToFinish[0] = queriesToFinish[0] - 1;
					if (endAdapter != null && queriesToFinish[0] == 0) {
						filterEnd();
					}
				}
			});
		}
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

	public List<ItemCategory> getCategories() {
		return categories;
	}

	public void setCategoryFilter(List<UserCategoryFilterListItem> categoryFilter) {
		this.categoryFilter = categoryFilter;
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
