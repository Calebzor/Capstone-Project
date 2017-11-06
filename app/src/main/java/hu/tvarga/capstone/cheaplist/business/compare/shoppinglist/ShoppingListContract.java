package hu.tvarga.capstone.cheaplist.business.compare.shoppinglist;

import android.support.v7.widget.RecyclerView;

import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

public interface ShoppingListContract {

	interface Presenter {

		void onPause();
		void onResume(View view);
		RecyclerView.Adapter<ShoppingListItemHolder> getAdapter();
		void removeFromList(ShoppingListItem item);
		void addToList(ShoppingListItem item);
	}

	interface View {

		void setEmptyView(int itemCount);
		android.view.View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
				ShoppingListItemHolder holder);
	}
}
