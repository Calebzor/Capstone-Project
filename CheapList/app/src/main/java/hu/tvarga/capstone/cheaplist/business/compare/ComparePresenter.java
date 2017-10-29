package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.business.broadcast.ObjectListener;
import hu.tvarga.capstone.cheaplist.business.broadcast.ObjectReceiver;
import hu.tvarga.capstone.cheaplist.business.broadcast.ObjectReceiverFactory;
import hu.tvarga.capstone.cheaplist.business.compare.dto.CategoriesBroadcastObject;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import timber.log.Timber;

@ApplicationScope
public class ComparePresenter implements CompareContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	private final CompareService compareService;
	private final ObjectReceiver<CategoriesBroadcastObject> categoriesBroadcastObjectObjectReceiver;
	private CompareContract.View view;

	private List<String> categories;
	@SuppressWarnings("FieldCanBeLocal")
	private ObjectListener<CategoriesBroadcastObject> categoriesBroadcastObjectObjectListener =
			new ObjectListener<CategoriesBroadcastObject>() {
				@Override
				public void onReceive(CategoriesBroadcastObject object) {
					Timber.d("CategoriesBroadcastObject#onReceive", object);
					categories = object.getCategories();
					view.notifyGotMerchantCategoryData(categories);
				}

				@Override
				public void onFailure(Throwable throwable) {
					Timber.d("CategoriesBroadcastObject#onFailure", throwable);
				}

				@Override
				public void onCancelled() {
					Timber.d("CategoriesBroadcastObject#onCancelled");
				}
			};

	@Inject
	ComparePresenter(ShoppingListManager shoppingListManager, CompareService compareService,
			ObjectReceiverFactory objectReceiverFactory) {
		categoriesBroadcastObjectObjectReceiver = objectReceiverFactory.get(
				categoriesBroadcastObjectObjectListener, CategoriesBroadcastObject.class);
		this.compareService = compareService;
		this.shoppingListManager = shoppingListManager;
		view = new CompareTabsViewStub();
	}

	@Override
	public void onResume(CompareContract.View view) {
		this.view = view;
		categoriesBroadcastObjectObjectReceiver.register();
		if (categories != null) {
			view.notifyGotMerchantCategoryData(categories);
		}
	}

	@Override
	public void onPause() {
		categoriesBroadcastObjectObjectReceiver.unregister();
		view = new CompareTabsViewStub();
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

}
