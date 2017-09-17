package hu.tvarga.capstone.cheaplist.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
public class ShoppingListManager implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReferenceUser;

	@Inject
	public ShoppingListManager() {
		firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.addAuthStateListener(this);
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if (currentUser != null) {
			databaseReferenceUser = firebaseDatabase.getReference().child("userData").child(
					currentUser.getUid()).child("shoppingList");
		}
		else {
			databaseReferenceUser = null;
		}
	}

	public void addToList(Item item, Merchant merchant) {
		if (databaseReferenceUser == null) {
			return;
		}
		ShoppingListItem shoppingListItem = new ShoppingListItem(item, merchant);
		databaseReferenceUser.child(item.id).setValue(shoppingListItem);
	}

	public void checkItem(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUser == null) {
			return;
		}
		shoppingListItem.checked = true;
		databaseReferenceUser.child(shoppingListItem.id).setValue(shoppingListItem);
	}

	public void unCheckItem(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUser == null) {
			return;
		}
		shoppingListItem.checked = false;
		databaseReferenceUser.child(shoppingListItem.id).setValue(shoppingListItem);
	}

	public void removeFromList(Item item) {
		if (databaseReferenceUser == null) {
			return;
		}
		databaseReferenceUser.child(item.id).setValue(null);
	}
}
