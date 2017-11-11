package hu.tvarga.capstone.cheaplist.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.MainActivity;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.ui.detail.DetailFragment.DETAIL_ITEM;

public class WidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new CheapListAppWidgetViewsFactory();
	}

	public class CheapListAppWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

		private List<ShoppingListItem> items = new ArrayList<>();
		private CollectionReference dbRefForShoppingList;

		CheapListAppWidgetViewsFactory() {
			setUPp();
		}

		private void setUPp() {
			listenForAuthStateChange();
			setUpShoppingListListener();
		}

		private void setUpShoppingListListener() {
			if (dbRefForShoppingList != null) {
				dbRefForShoppingList.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(QuerySnapshot documentSnapshots,
							FirebaseFirestoreException e) {
						if (e != null) {
							Timber.e("widget dbRefForShoppingList#onEvent");
							return;
						}

						List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
						items.clear();
						for (DocumentSnapshot document : documents) {
							ShoppingListItem shoppingListItem = document.toObject(
									ShoppingListItem.class);
							items.add(shoppingListItem);
							sortByCheckedState();
							updateWidget();
						}

					}
				});
			}
		}

		private void sortByCheckedState() {
			Collections.sort(items, new Comparator<ShoppingListItem>() {
				@Override
				public int compare(ShoppingListItem i1, ShoppingListItem i2) {
					if (i1.checked && !i2.checked) {
						return 1;
					}
					else {
						return -1;
					}
				}
			});
		}

		private void listenForAuthStateChange() {
			FirebaseAuth auth = FirebaseAuth.getInstance();
			FirebaseAuth.AuthStateListener authStateListener =
					new FirebaseAuth.AuthStateListener() {
						@Override
						public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
							FirebaseUser currentUser = firebaseAuth.getCurrentUser();
							if (currentUser != null) {
								dbRefForShoppingList = FirebaseFirestore.getInstance().collection(
										"userData").document(currentUser.getUid()).collection(
										"shoppingList");
								setUpShoppingListListener();
							}
							else {
								dbRefForShoppingList = null;
							}
						}

					};
			auth.addAuthStateListener(authStateListener);
		}

		void updateWidget() {
			Intent intent = new Intent(getApplicationContext(), WidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			int[] ids = {R.xml.widget_provider};
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
			getApplicationContext().sendBroadcast(intent);
		}

		@Override
		public void onCreate() {
			Timber.d("CheapListAppWidgetViewsFactory#onCreate");
		}

		@Override
		public void onDataSetChanged() {
			Timber.d("CheapListAppWidgetViewsFactory#onDataSetChanged");
		}

		@Override
		public void onDestroy() {
			Timber.d("CheapListAppWidgetViewsFactory#onDestroy");
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews row = new RemoteViews(getPackageName(), R.layout.widget_shopping_list_item);
			if (items != null) {
				ShoppingListItem shoppingListItem = items.get(position);
				row.setTextViewText(R.id.widgetItemName, shoppingListItem.name);
				row.setTextViewText(R.id.widgetItemPrice,
						String.format("%s - %s %s", shoppingListItem.merchant.name,
								shoppingListItem.price, shoppingListItem.currency));

				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				Bundle extras = new Bundle();
				extras.putSerializable(DETAIL_ITEM, shoppingListItem);
				intent.putExtras(extras);

				row.setOnClickFillInIntent(R.id.widgetListItemContainer, intent);
			}
			return row;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
