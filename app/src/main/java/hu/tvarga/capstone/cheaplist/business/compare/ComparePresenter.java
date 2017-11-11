package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
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

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.capstone.cheaplist.utility.StringUtils;
import hu.tvarga.capstone.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

public class ComparePresenter implements CompareContract.Presenter {

	private static final int MAX_IMAGE_PRELOAD_SIZE = 10;
	private final ShoppingListManager shoppingListManager;
	private final CompareService compareService;
	private final Event event;
	CompareContract.View view;

	List<ItemCategory> categories;

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
		view.notifyGotMerchantCategoryData(categories);
	}

	@Override
	public void onPause() {
		event.unregister(this);
		view = new CompareTabsViewStub();
	}

	@Subscribe
	public void handleCategoriesBroadcastObject(CategoriesBroadcastObject object) {
		Timber.d("handleCategoriesBroadcastObject", object);
		categories = object.getCategories();
		view.notifyGotMerchantCategoryData(categories);
	}

	@Override
	public RecyclerView.Adapter<MerchantCategoryListItemHolder> getAndSetStartAdapter(
			RecyclerView view) {
		List<MerchantCategoryListItem> startItems = compareService.getStartItems();
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter = getAdapter(
				R.layout.merchant_category_list_item_start, startItems, true);

		view.setAdapter(adapter);
		setImagePreLoader(view, startItems);
		compareService.setStartAdapter(adapter);
		return adapter;
	}

	@Override
	public RecyclerView.Adapter<MerchantCategoryListItemHolder> getAndSetEndAdapter(
			RecyclerView view) {
		List<MerchantCategoryListItem> endItems = compareService.getEndItems();
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter = getAdapter(
				R.layout.merchant_category_list_item_end, endItems, false);
		view.setAdapter(adapter);
		setImagePreLoader(view, endItems);
		compareService.setEndAdapter(adapter);
		return adapter;
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
			return Glide.with(view).load(item).apply(
					new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL));
		}
	}

	private boolean stillLoadingUserFilter(int size) {
		return size == 0 && !compareService.isUserFilterLoaded();
	}

	@Override
	public SearchView.OnQueryTextListener getOnQueryTextListener() {
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

}
