package hu.tvarga.capstone.cheaplist.business.itemdetail;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

public class DetailPresenter implements DetailContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	private DetailContract.View view;
	private ShoppingListItem item;

	private DatabaseReference itemRef;
	private ValueEventListener itemEventListener;
	private DatabaseReference shoppingListItemRef;
	private ValueEventListener shoppingListItemEventListener;

	@Inject
	public DetailPresenter(ShoppingListManager shoppingListManager) {
		view = new DetailViewStub();
		this.shoppingListManager = shoppingListManager;
	}

	@Override
	public String getManufacturerInformation(ManufacturerInformation manufacturerInformation) {
		StringBuilder sb = new StringBuilder();
		if (manufacturerInformation.address != null) {
			sb.append(manufacturerInformation.address).append("\n");
		}
		if (manufacturerInformation.contact != null) {
			sb.append(manufacturerInformation.contact).append("\n");
		}
		if (manufacturerInformation.supplier != null) {
			sb.append(manufacturerInformation.supplier).append("\n");
		}
		String string = sb.toString();
		return string;
	}

	@Override
	public void onResume(DetailContract.View view, ShoppingListItem itemFromArgument) {
		this.view = view;
		this.item = itemFromArgument;
		loadData();
	}

	@Override
	public void onPause() {
		removeDBListeners();
		this.item = null;
		this.view = new DetailViewStub();
	}

	@Override
	public void removeItemFromShoppingList() {
		shoppingListManager.removeFromList(item);
	}

	public void loadData() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null) {
			shoppingListItemEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					final ShoppingListItem shoppingListItem = dataSnapshot.getValue(
							ShoppingListItem.class);
					Timber.d("listItemChange %s", shoppingListItem);
					if (shoppingListItem != null) {
						view.showFabAsRemove();
					}
					else {
						view.showFabAsAdd();
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Timber.d("shoppingListItemEventListener#onCancelled %s", databaseError);
				}
			};
			shoppingListItemRef = FirebaseDatabase.getInstance().getReference().child("userData")
					.child(currentUser.getUid()).child("shoppingList").child(item.id);
			shoppingListItemRef.addValueEventListener(shoppingListItemEventListener);
			itemRef = FirebaseDatabase.getInstance().getReference().child("publicReadable").child(
					"items").child(item.id);
			itemEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					Item item = dataSnapshot.getValue(Item.class);
					Timber.d("items %s", item);
					if (item != null) {
						view.updateUI(item);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Timber.d("itemEventListener#onCancelled %s", databaseError);
				}
			};
			itemRef.addValueEventListener(itemEventListener);
		}
	}

	private void removeDBListeners() {
		if (shoppingListItemRef != null) {
			shoppingListItemRef.removeEventListener(shoppingListItemEventListener);
		}
		if (itemRef != null) {
			itemRef.removeEventListener(itemEventListener);
		}
	}

	@Override
	public void addToShoppingList(ShoppingListItem itemFromArgument, Merchant merchant) {
		shoppingListManager.addToList(itemFromArgument, merchant);
	}
}
