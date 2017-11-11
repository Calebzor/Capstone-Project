package hu.tvarga.capstone.cheaplist.business.compare;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import java.util.List;

import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.MerchantCategoryListItemHolder;

public interface CompareContract {

	interface Presenter {

		void onResume(View view);
		void onPause();
		RecyclerView.Adapter<MerchantCategoryListItemHolder> getStartAdapter(
				RecyclerView startItems);
		RecyclerView.Adapter<MerchantCategoryListItemHolder> getEndAdapter(RecyclerView endItems);
		SearchView.OnQueryTextListener getOnQueryTextListener();
	}

	interface View {

		android.view.View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
				MerchantCategoryListItemHolder holder);
		android.view.View getActivityCoordinatorLayout();
		void setStartEmptyView(int itemCount);
		void setEndEmptyView(int itemCount);
		void notifyGotMerchantCategoryData(List<ItemCategory> categories);
	}
}
