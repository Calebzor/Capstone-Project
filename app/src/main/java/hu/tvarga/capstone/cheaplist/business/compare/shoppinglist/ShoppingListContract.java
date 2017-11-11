package hu.tvarga.capstone.cheaplist.business.compare.shoppinglist;

import android.support.v7.widget.RecyclerView;

import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListItemHolder;

public interface ShoppingListContract {

	interface Presenter {

		void onPause();
		void onResume(View view);
		void adapterOnBindViewHolder(ShoppingListItemHolder holder, int position);
		int adapterGetItemCount();
		void removeFromList(ShoppingListItem item);
		void addToList(ShoppingListItem item);
		void setAdapter(RecyclerView.Adapter<ShoppingListItemHolder> adapter);
	}

	interface View {

		void setEmptyView(int itemCount);
		android.view.View.OnClickListener getOnListItemOnClickListener(ShoppingListItem item,
				ShoppingListItemHolder holder);
	}
}
