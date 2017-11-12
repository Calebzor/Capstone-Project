package hu.tvarga.cheaplist.business.compare;

import android.view.View;

import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.MerchantCategoryListItemHolder;
import timber.log.Timber;

public class CompareTabsViewStub implements CompareContract.View {

	@Override
	public View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
			MerchantCategoryListItemHolder holder) {
		Timber.d("getOnListItemOnClickListener");
		return null;
	}

	@Override
	public View getActivityCoordinatorLayout() {
		Timber.d("getActivityCoordinatorLayout");
		return null;
	}

	@Override
	public void setStartEmptyView(int itemCount) {
		Timber.d("setStartEmptyView");
	}

	@Override
	public void setEndEmptyView(int itemCount) {
		Timber.d("setEndEmptyView");
	}
}
