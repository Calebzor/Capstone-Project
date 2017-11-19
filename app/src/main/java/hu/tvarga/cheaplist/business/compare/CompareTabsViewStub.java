package hu.tvarga.cheaplist.business.compare;

import android.view.View;

import hu.tvarga.cheaplist.business.utility.BaseViewStub;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.MerchantCategoryListItemHolder;

public class CompareTabsViewStub extends BaseViewStub implements CompareContract.View {

	@Override
	public View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
			MerchantCategoryListItemHolder holder) {
		log("getOnListItemOnClickListener");
		return null;
	}

	@Override
	public View getActivityCoordinatorLayout() {
		log("getActivityCoordinatorLayout");
		return null;
	}

	@Override
	public void setStartEmptyView(int itemCount) {
		log("setStartEmptyView");
	}

	@Override
	public void setEndEmptyView(int itemCount) {
		log("setEndEmptyView");
	}
}
