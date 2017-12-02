package hu.tvarga.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.MerchantCategoryListItemHolder;

public interface CompareContract {

	interface Presenter {

		void onResume(View view);
		void onPause();
		void setStartAdapter(RecyclerView startItems);
		void setEndAdapter(RecyclerView endItems);
		SearchView.OnQueryTextListener getOnQueryTextListener();
		String getFilter();
	}

	interface View {

		android.view.View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
				MerchantCategoryListItemHolder holder);
		android.view.View getActivityCoordinatorLayout();
		void setStartEmptyView(int itemCount);
		void setEndEmptyView(int itemCount);
	}
}
