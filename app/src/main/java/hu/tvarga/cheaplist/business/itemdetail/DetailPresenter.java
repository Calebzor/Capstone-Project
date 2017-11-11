package hu.tvarga.cheaplist.business.itemdetail;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.dao.Item;
import hu.tvarga.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

import static hu.tvarga.cheaplist.business.compare.CompareService.ITEMS;
import static hu.tvarga.cheaplist.business.compare.CompareService.MERCHANT_ITEMS;

public class DetailPresenter implements DetailContract.Presenter {

	private final ShoppingListManager shoppingListManager;
	private DetailContract.View view;
	private ShoppingListItem item;

	private ListenerRegistration shoppingListItemListenerRegistration;
	private ListenerRegistration itemRefListenerRegistration;

	@Inject
	DetailPresenter(ShoppingListManager shoppingListManager) {
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
		return sb.toString();
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

	private void loadData() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null) {
			EventListener<DocumentSnapshot> shoppingListItemEventListener =
					new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(DocumentSnapshot documentSnapshot,
								FirebaseFirestoreException e) {
							if (e != null) {
								Timber.e("shoppingListItemEventListener#onCancelled %s", e);
								return;
							}

							if (documentSnapshot != null && documentSnapshot.exists()) {
								ShoppingListItem shoppingListItem = documentSnapshot.toObject(
										ShoppingListItem.class);
								Timber.d("listItemChange %s", shoppingListItem);
								view.showFabAsRemove();
							}
							else {
								view.showFabAsAdd();
							}
						}
					};
			DocumentReference shoppingListItemRef = FirebaseFirestore.getInstance().collection(
					"userData").document(currentUser.getUid()).collection("shoppingList").document(
					item.id);
			shoppingListItemListenerRegistration = shoppingListItemRef.addSnapshotListener(
					shoppingListItemEventListener);
			DocumentReference itemRef = FirebaseFirestore.getInstance().collection(MERCHANT_ITEMS)
					.document(item.merchant.id).collection(ITEMS).document(item.id);

			itemRefListenerRegistration = itemRef.addSnapshotListener(
					new EventListener<DocumentSnapshot>() {
						@Override
						public void onEvent(DocumentSnapshot documentSnapshot,
								FirebaseFirestoreException e) {
							if (e != null) {
								Timber.e("itemEventListener#onCancelled %s", e);
								return;
							}
							if (documentSnapshot != null) {
								Item itemFromDB = documentSnapshot.toObject(Item.class);
								Timber.d("items %s", itemFromDB);
								view.updateUI(itemFromDB);
							}
						}
					});
		}
	}

	private void removeDBListeners() {
		if (shoppingListItemListenerRegistration != null) {
			shoppingListItemListenerRegistration.remove();
		}
		if (itemRefListenerRegistration != null) {
			itemRefListenerRegistration.remove();
		}
	}

	@Override
	public void addToShoppingList(ShoppingListItem itemFromArgument, Merchant merchant) {
		shoppingListManager.addToList(itemFromArgument, merchant);
	}
}
