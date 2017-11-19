package hu.tvarga.cheaplist.business.compare.shoppinglist;

import android.view.View;

import hu.tvarga.cheaplist.business.utility.BaseViewStub;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

public class ShoppingListBaseViewStub extends BaseViewStub implements ShoppingListContract.View {

	@Override
	public void setEmptyView(int itemCount) {
		log("setEmptyView");
	}

	@Override
	public View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
			ShoppingListItemHolder holder) {
		log("getOnListItemOnClickListener");
		return null;
	}
}
