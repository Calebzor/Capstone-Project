package hu.tvarga.capstone.cheaplist.business.compare.shoppinglist;

import android.view.View;

import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListItemHolder;
import timber.log.Timber;

public class ShoppingListViewStub implements ShoppingListContract.View {

	@Override
	public void setEmptyView(int itemCount) {
		Timber.d("setEmptyView");
	}

	@Override
	public View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
			ShoppingListItemHolder holder) {
		Timber.d("getOnListItemOnClickListener");
		return null;
	}
}
