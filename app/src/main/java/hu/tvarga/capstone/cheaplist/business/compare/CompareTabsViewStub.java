package hu.tvarga.capstone.cheaplist.business.compare;

import android.view.View;

import java.util.List;

import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;
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

	@Override
	public void notifyGotMerchantCategoryData(List<ItemCategory> categories) {
		Timber.d("notifyGotMerchantCategoryData");
	}

}
