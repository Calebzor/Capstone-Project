package hu.tvarga.capstone.cheaplist.business.itemdetail;

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;

public interface DetailContract {

	interface Presenter {

		void addToShoppingList(ShoppingListItem itemFromArgument, Merchant merchant);
		String getManufacturerInformation(ManufacturerInformation manufacturerInformation);
		void onResume(View view, ShoppingListItem itemFromArgument);
		void onPause();
		void removeItemFromShoppingList();
	}

	interface View {

		void updateUI(Item item);
		void showFabAsRemove();
		void showFabAsAdd();
	}
}
