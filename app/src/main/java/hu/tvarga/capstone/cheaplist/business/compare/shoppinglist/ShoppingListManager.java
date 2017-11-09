package hu.tvarga.capstone.cheaplist.business.compare.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListItemHolder;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.business.analytics.AnalyticsEvents.ITEM_ADD_TO_SHOPPING_LIST;
import static hu.tvarga.capstone.cheaplist.business.analytics.AnalyticsEvents.ITEM_REMOVE_FROM_SHOPPING_LIST;

@ApplicationScope
public class ShoppingListManager implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReferenceUserShoppingList;
	private FirebaseAnalytics firebaseAnalytics;

	private List<ShoppingListItem> items = new LinkedList<>();
	private RecyclerView.Adapter<ShoppingListItemHolder> adapter;

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
			databaseReferenceUserShoppingList = firebaseDatabase.getReference().child("userData")
					.child(currentUser.getUid()).child("shoppingList");
			getItemsFromDB();
		}
		else {
			databaseReferenceUserShoppingList = null;
		}
	}

	public void addToList(Item item, Merchant merchant) {
		ShoppingListItem shoppingListItem = new ShoppingListItem(item, merchant);
		addToList(shoppingListItem);
	}

	void addToList(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUserShoppingList == null) {
			return;
		}
		trackShoppingListEvent(ITEM_ADD_TO_SHOPPING_LIST, shoppingListItem);
		databaseReferenceUserShoppingList.child(shoppingListItem.id).setValue(shoppingListItem);
	}

	public void checkItem(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUserShoppingList == null) {
			return;
		}
		shoppingListItem.checked = true;
		databaseReferenceUserShoppingList.child(shoppingListItem.id).setValue(shoppingListItem);
	}

	public void unCheckItem(ShoppingListItem shoppingListItem) {
		if (databaseReferenceUserShoppingList == null) {
			return;
		}
		shoppingListItem.checked = false;
		databaseReferenceUserShoppingList.child(shoppingListItem.id).setValue(shoppingListItem);
	}

	public void removeFromList(Item item) {
		if (databaseReferenceUserShoppingList == null) {
			return;
		}
		trackShoppingListEvent(ITEM_REMOVE_FROM_SHOPPING_LIST, item);
		databaseReferenceUserShoppingList.child(item.id).setValue(null);
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

	private void getItemsFromDB() {
		Query query = databaseReferenceUserShoppingList.orderByChild("checked");
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Iterable<DataSnapshot> children = dataSnapshot.getChildren();
				final List<ShoppingListItem> newItems = new LinkedList<>();
				for (DataSnapshot child : children) {
					ShoppingListItem item = child.getValue(ShoppingListItem.class);
					newItems.add(item);
				}

				if (adapter != null) {
					DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
						@Override
						public int getOldListSize() {
							return items.size();
						}

						@Override
						public int getNewListSize() {
							return newItems.size();
						}

						@Override
						public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
							return items.get(oldItemPosition).id.equals(
									newItems.get(newItemPosition).id);
						}

						@Override
						public boolean areContentsTheSame(int oldItemPosition,
								int newItemPosition) {
							return items.get(oldItemPosition).toString().equals(
									newItems.get(newItemPosition).toString());
						}
					});
					result.dispatchUpdatesTo(adapter);
				}
				items = newItems;
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Timber.d("getStartItemsFromDB#onCancelled %s", databaseError);
			}
		});
	}

	void setAdapter(RecyclerView.Adapter<ShoppingListItemHolder> adapter) {
		this.adapter = adapter;
	}

	List<ShoppingListItem> getItems() {
		return items;
	}
}
