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
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import hu.tvarga.capstone.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

public class ComparePresenter implements CompareContract.Presenter {

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
		if (categories != null) {
			view.notifyGotMerchantCategoryData(categories);
		}
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
										compareService.getStartMerchant()), holder));
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
										holder));
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
