package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.capstone.cheaplist.utility.EventBusWrapper;
import timber.log.Timber;

@ApplicationScope
public class ComparePresenter implements CompareContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	private final CompareService compareService;
	private final EventBusWrapper eventBusWrapper;
	CompareContract.View view;

	List<String> categories;

	@Inject
	ComparePresenter(ShoppingListManager shoppingListManager, CompareService compareService,
			EventBusWrapper eventBusWrapper) {
		this.compareService = compareService;
		this.shoppingListManager = shoppingListManager;
		this.eventBusWrapper = eventBusWrapper;
		view = new CompareTabsViewStub();
	}

	@Override
	public void onResume(CompareContract.View view) {
		this.view = view;
		eventBusWrapper.getDefault().register(this);
		if (categories != null) {
			view.notifyGotMerchantCategoryData(categories);
		}
	}

	@Override
	public void onPause() {
		eventBusWrapper.getDefault().unregister(this);
		view = new CompareTabsViewStub();
	}

	@Subscribe
	public void handleCategoriesBroadcastObject(CategoriesBroadcastObject object) {
		Timber.d("CategoriesBroadcastObject#onReceive", object);
		categories = object.getCategories();
		view.notifyGotMerchantCategoryData(categories);
	}

	@Override
	public RecyclerView.Adapter<MerchantCategoryListItemHolder> getStartAdapter() {
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter =
				new RecyclerView.Adapter<MerchantCategoryListItemHolder>() {
					@Override
					public MerchantCategoryListItemHolder onCreateViewHolder(ViewGroup parent,
							int viewType) {
						View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
								R.layout.merchant_category_list_item_start, parent, false);
						return new MerchantCategoryListItemHolder(viewHolder);
					}

					@Override
					public void onBindViewHolder(MerchantCategoryListItemHolder holder,
							int position) {
						MerchantCategoryListItem item = compareService.getStartItems().get(
								position);
						holder.bind(item, view.getActivityCoordinatorLayout(), shoppingListManager,
								compareService.getStartMerchant(),
								view.getOnListItemOnClickListener(new ShoppingListItem(item,
										compareService.getStartMerchant()), holder), position);
					}

					@Override
					public int getItemCount() {
						int size = compareService.getStartItems().size();
						view.setStartEmptyView(size);
						return size;
					}
				};
		compareService.setStartAdapter(adapter);
		return adapter;
	}

	@Override
	public RecyclerView.Adapter<MerchantCategoryListItemHolder> getEndAdapter() {
		RecyclerView.Adapter<MerchantCategoryListItemHolder> adapter =
				new RecyclerView.Adapter<MerchantCategoryListItemHolder>() {
					@Override
					public MerchantCategoryListItemHolder onCreateViewHolder(ViewGroup parent,
							int viewType) {
						View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
								R.layout.merchant_category_list_item_end, parent, false);
						return new MerchantCategoryListItemHolder(viewHolder);
					}

					@Override
					public void onBindViewHolder(MerchantCategoryListItemHolder holder,
							int position) {
						MerchantCategoryListItem item = compareService.getEndItems().get(position);
						holder.bind(item, view.getActivityCoordinatorLayout(), shoppingListManager,
								compareService.getEndMerchant(), view.getOnListItemOnClickListener(
										new ShoppingListItem(item, compareService.getEndMerchant()),
										holder), position);
					}

					@Override
					public int getItemCount() {
						int size = compareService.getEndItems().size();
						view.setEndEmptyView(size);
						return size;
					}
				};
		compareService.setEndAdapter(adapter);
		return adapter;
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
