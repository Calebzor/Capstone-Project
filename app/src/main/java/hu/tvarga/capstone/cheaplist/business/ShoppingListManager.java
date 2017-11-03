package hu.tvarga.capstone.cheaplist.business;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

import static hu.tvarga.capstone.cheaplist.business.analytics.AnalyticsEvents.ITEM_ADD_TO_SHOPPING_LIST;
import static hu.tvarga.capstone.cheaplist.business.analytics.AnalyticsEvents.ITEM_REMOVE_FROM_SHOPPING_LIST;

@ApplicationScope
public class ShoppingListManager implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReferenceUser;
	private FirebaseAnalytics firebaseAnalytics;

	@Inject
	public ShoppingListManager(Context context) {
		firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.addAuthStateListener(this);
		firebaseAnalytics = FirebaseAnalytics.getInstance(context);
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
		ShoppingListItem shoppingListItem = new ShoppingListItem(item, merchant);
		addToList(shoppingListItem);
	}

	public void addToList(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUser == null) {
			return;
		}
		trackShoppingListEvent(ITEM_ADD_TO_SHOPPING_LIST, shoppingListItem);
		databaseReferenceUser.child(shoppingListItem.id).setValue(shoppingListItem);
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
		trackShoppingListEvent(ITEM_REMOVE_FROM_SHOPPING_LIST, item);
		databaseReferenceUser.child(item.id).setValue(null);
	}

	private void trackShoppingListEvent(String event, Item item) {
		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.id);
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, item.name);
		bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(item.price));
		bundle.putString(FirebaseAnalytics.Param.CURRENCY, item.currency);
		bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, String.valueOf(item.category));
		firebaseAnalytics.logEvent(event, bundle);
	}
}
