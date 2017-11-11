package hu.tvarga.cheaplist.business.compare.shoppinglist;

import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

public class ShoppingListPresenter implements ShoppingListContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	ShoppingListContract.View view;

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
	public void adapterOnBindViewHolder(ShoppingListItemHolder holder, int position) {
		ShoppingListItem item = shoppingListManager.getItems().get(position);
		holder.bind(item, shoppingListManager, view.getOnListItemOnClickListener(item, holder));
	}

	@Override
	public int adapterGetItemCount() {
		int size = shoppingListManager.getItems().size();
		view.setEmptyView(size);
		return size;
	}

	@Override
	public void removeFromList(ShoppingListItem item) {
		shoppingListManager.removeFromList(item);
	}

	@Override
	public void addToList(ShoppingListItem item) {
		shoppingListManager.addToList(item);
	}

	@Override
	public void setAdapter(RecyclerView.Adapter<ShoppingListItemHolder> adapter) {
		shoppingListManager.setAdapter(adapter);
	}
}
