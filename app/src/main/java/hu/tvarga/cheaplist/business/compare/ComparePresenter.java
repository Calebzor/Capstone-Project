package hu.tvarga.cheaplist.business.compare;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.cheaplist.utility.StringUtils;
import hu.tvarga.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

public class ComparePresenter implements CompareContract.Presenter {

	private static final int MAX_IMAGE_PRELOAD_SIZE = 10;
	private final ShoppingListManager shoppingListManager;
	private final CompareService compareService;
	private final Event event;
	CompareContract.View view;

	List<ItemCategory> categories;

	SearchView searchView;

	@Inject
	ComparePresenter(ShoppingListManager shoppingListManager, CompareService compareService,
			Event event) {
		this.compareService = compareService;
		this.shoppingListManager = shoppingListManager;
		this.event = event;
		view = new CompareTabsViewStub();
	}

	@Override
	public void onResume(CompareContract.View view) {
		this.view = view;
		event.register(this);
		if (categories == null) {
			categories = compareService.getCategories();
		}
	}

	@Override
	public void onPause() {
		searchView.setOnQueryTextListener(null);
		event.unregister(this);
		view = new CompareTabsViewStub();
	}

	@Subscribe
	public void handleCategoriesBroadcastObject(CategoriesBroadcastObject object) {
		Timber.d("handleCategoriesBroadcastObject", object);
		categories = object.getCategories();
	}

	@Override
	public void setStartAdapter(RecyclerView view) {
		List<MerchantCategoryListItem> startItems = compareService.getStartItems();
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter = getAdapter(
				R.layout.merchant_category_list_item_start, startItems, true);

		view.setAdapter(adapter);
		setImagePreLoader(view, startItems);
		compareService.setStartAdapter(adapter);
	}

	@Override
	public void setEndAdapter(RecyclerView view) {
		List<MerchantCategoryListItem> endItems = compareService.getEndItems();
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter = getAdapter(
				R.layout.merchant_category_list_item_end, endItems, false);
		view.setAdapter(adapter);
		setImagePreLoader(view, endItems);
		compareService.setEndAdapter(adapter);
	}

	private RecyclerView.Adapter<MerchantCategoryListItemHolder> getAdapter(
			@LayoutRes final int layoutId, final List<MerchantCategoryListItem> items,
			final boolean isStart) {
		return new RecyclerView.Adapter<MerchantCategoryListItemHolder>() {
			@Override
			public MerchantCategoryListItemHolder onCreateViewHolder(ViewGroup parent,
					int viewType) {
				View viewHolder = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,
						false);
				return new MerchantCategoryListItemHolder(viewHolder);
			}

			@Override
			public void onBindViewHolder(MerchantCategoryListItemHolder holder, int position) {
				MerchantCategoryListItem item = items.get(position);
				Merchant merchant = compareService.getEndMerchant();
				if (isStart) {
					merchant = compareService.getStartMerchant();
				}

				holder.bind(item, view.getActivityCoordinatorLayout(), shoppingListManager,
						merchant,
						view.getOnListItemOnClickListener(new ShoppingListItem(item, merchant),
								holder));
			}

			@Override
			public int getItemCount() {
				int size = items.size();
				if (!stillLoadingUserFilter(size)) {
					if (isStart) {
						view.setStartEmptyView(size);
					}
					else {
						view.setEndEmptyView(size);
					}
				}
				return size;
			}
		};
	}

	private void setImagePreLoader(RecyclerView recyclerView,
			List<MerchantCategoryListItem> items) {
		ListPreloader.PreloadSizeProvider<String> sizeProvider = new FixedPreloadSizeProvider<>(
				Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
		ListPreloader.PreloadModelProvider<String> modelProvider =
				new CompareListImagePreloadModelProvider(items, recyclerView);
		RecyclerViewPreloader<String> preLoader = new RecyclerViewPreloader<>(
				Glide.with(recyclerView), modelProvider, sizeProvider, MAX_IMAGE_PRELOAD_SIZE);
		recyclerView.addOnScrollListener(preLoader);
	}

	class CompareListImagePreloadModelProvider
			implements ListPreloader.PreloadModelProvider<String> {

		private final List<MerchantCategoryListItem> items;
		private final View view;

		CompareListImagePreloadModelProvider(List<MerchantCategoryListItem> items, View view) {
			this.items = items;
			this.view = view;
		}

		@NonNull
		@Override
		public List<String> getPreloadItems(int position) {
			String url = items.get(position).imageURL;
			if (StringUtils.isEmpty(url)) {
				return Collections.emptyList();
			}
			return Collections.singletonList(url);
		}

		@Nullable
		@Override
		public RequestBuilder getPreloadRequestBuilder(String item) {
			if (UserService.shouldDownloadImages()) {
				return Glide.with(view).load(item).apply(
						new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL));
			}
			else {
				return Glide.with(view).load(R.drawable.image_placeholder);
			}
		}
	}

	private boolean stillLoadingUserFilter(int size) {
		return size == 0 && !compareService.isUserFilterLoaded();
	}

	private SearchView.OnQueryTextListener getOnQueryTextListener() {
		return new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				compareService.setFilter(query);
				return false;
			}
		};
	}

	@Override
	public void setOnQueryTextListener(SearchView searchView, MenuItem item) {
		this.searchView = searchView;
		String filter = compareService.getFilter();
		if (!StringUtils.isEmpty(filter)) {
			item.expandActionView();
			searchView.setQuery(filter, false);
		}
		searchView.setOnQueryTextListener(getOnQueryTextListener());
	}

}
