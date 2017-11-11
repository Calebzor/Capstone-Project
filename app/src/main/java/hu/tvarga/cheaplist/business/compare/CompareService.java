package hu.tvarga.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import hu.tvarga.cheaplist.BuildConfig;
import hu.tvarga.cheaplist.business.UserService;
import hu.tvarga.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.cheaplist.utility.StringUtils;
import hu.tvarga.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

@ApplicationScope
public class CompareService {

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
	private List<UserCategoryFilterListItem> categoryFilter = new LinkedList<>();
	private String filter;
	private final FirebaseFirestore db;
	private List<ListenerRegistration> startListeners = new LinkedList<>();
	private List<ListenerRegistration> endListeners = new LinkedList<>();
	private boolean userFilterLoaded;

	@Inject
	CompareService(Event event, UserService userService) {
		this.event = event;
		db = FirebaseFirestore.getInstance();
		FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		FirebaseRemoteConfigSettings configSettings =
				new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(
						BuildConfig.DEBUG).build();
		firebaseRemoteConfig.setConfigSettings(configSettings);

		getCategoriesFromDB();
		getMerchantsFromDB();
		event.register(this);
		userService.initiateGetCategoriesFilterForUser();
	}

	@Subscribe
	public void handleCompareSettingsFilterChangedBroadcastObject(
			CompareSettingsFilterChangedBroadcastObject object) {
		userFilterLoaded = true;
		filterChanged(object);
	}

	boolean isUserFilterLoaded() {
		return userFilterLoaded;
	}

	private void filterChanged(CompareSettingsFilterChangedBroadcastObject object) {
		setCategoryFilter(object.getNewFilter());
		getData();
		event.unregister(this);
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
		boolean filtering = false;
		for (UserCategoryFilterListItem filterListItem : categoryFilter) {
			if (filterListItem.checked) {
				filtering = true;
				queries.add(query.whereEqualTo(CATEGORY_KEY, filterListItem.category.toString()));
			}
		}
		if (!filtering) {
			queries.add(query);
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
		getItemsFromDB(startListeners, startMerchantItemsDBRef, startItems, startItemsUnfiltered,
				startAdapter);
	}

	private void getEndItemsFromDB() {
		getItemsFromDB(endListeners, endMerchantItemsDBRef, endItems, endItemsUnfiltered,
				endAdapter);
	}

	private void getItemsFromDB(List<ListenerRegistration> listeners, List<Query> queries,
			final List<MerchantCategoryListItem> items,
			final List<MerchantCategoryListItem> unfilteredItems,
			final RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter) {
		cleanUpCategoryListListeners(listeners);

		final int[] queriesToFinish = {queries.size()};
		unfilteredItems.clear();
		for (Query query : queries) {
			ListenerRegistration listener = query.orderBy("name").addSnapshotListener(
					new EventListener<QuerySnapshot>() {
						@Override
						public void onEvent(QuerySnapshot documentSnapshots,
								FirebaseFirestoreException e) {
							if (e != null) {
								Timber.e(e, "getItemsFromDB#error %s");
								return;
							}
							List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
							for (DocumentSnapshot document : documents) {
								MerchantCategoryListItem item = document.toObject(
										MerchantCategoryListItem.class);
								unfilteredItems.add(item);
							}
							queriesToFinish[0] = queriesToFinish[0] - 1;
							if (adapter != null && queriesToFinish[0] == 0) {
								filter(items, unfilteredItems, adapter);
							}
						}
					});
			listeners.add(listener);
		}
	}

	private void cleanUpCategoryListListeners(List<ListenerRegistration> listeners) {
		for (ListenerRegistration startListener : listeners) {
			startListener.remove();
		}
		listeners.clear();
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

	private void filter(List<MerchantCategoryListItem> items,
			List<MerchantCategoryListItem> itemsUnfiltered,
			RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter) {
		items.clear();
		items.addAll(itemsUnfiltered);
		if (StringUtils.isEmpty(filter)) {
			adapter.notifyDataSetChanged();
			return;
		}
		removeItemFromStartList(items);
		adapter.notifyDataSetChanged();
	}

	private void removeItemFromStartList(List<MerchantCategoryListItem> items) {
		for (Iterator<MerchantCategoryListItem> iterator = items.iterator(); iterator.hasNext(); ) {
			MerchantCategoryListItem item = iterator.next();
			if (!item.name.toLowerCase(Locale.getDefault()).contains(
					filter.toLowerCase(Locale.getDefault()))) {
				iterator.remove();
			}
		}
	}

	void setFilter(String filter) {
		this.filter = filter;
		filter(startItems, startItemsUnfiltered, startAdapter);
		filter(endItems, endItemsUnfiltered, endAdapter);
	}

}
