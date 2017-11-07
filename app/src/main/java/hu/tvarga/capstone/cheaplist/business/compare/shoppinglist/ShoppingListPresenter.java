package hu.tvarga.capstone.cheaplist.business.compare.shoppinglist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

public class ShoppingListPresenter implements ShoppingListContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	private ShoppingListContract.View view;

	@Inject
	public ShoppingListPresenter(ShoppingListManager shoppingListManager) {
		this.shoppingListManager = shoppingListManager;
		view = new ShoppingListViewStub();
	}

	@Override
	public void onPause() {
		view = new ShoppingListViewStub();
	}

	@Override
	public void onResume(ShoppingListContract.View view) {
		this.view = view;
	}

	@Override
	public RecyclerView.Adapter<ShoppingListItemHolder> getAdapter() {
		RecyclerView.Adapter<ShoppingListItemHolder> adapter =
				new RecyclerView.Adapter<ShoppingListItemHolder>() {
					@Override
					public ShoppingListItemHolder onCreateViewHolder(ViewGroup parent,
							int viewType) {
						View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
								R.layout.shopping_list_item, parent, false);
						return new ShoppingListItemHolder(viewHolder);
					}

					@Override
					public void onBindViewHolder(ShoppingListItemHolder holder, int position) {
						ShoppingListItem item = shoppingListManager.getItems().get(position);
						holder.bind(item, shoppingListManager,
								view.getOnListItemOnClickListener(item, holder));
					}

					@Override
					public int getItemCount() {
						int size = shoppingListManager.getItems().size();
						view.setEmptyView(size);
						return size;
					}
				};
		shoppingListManager.setAdapter(adapter);
		return adapter;
	}

	@Override
	public void removeFromList(ShoppingListItem item) {
		shoppingListManager.removeFromList(item);
	}

	@Override
	public void addToList(ShoppingListItem item) {
		shoppingListManager.addToList(item);
	}
}
