package hu.tvarga.capstone.cheaplist.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListActivity;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;

public class WidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new CheapListAppWidgetViewsFactory();
	}

	public class CheapListAppWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

		private List<ShoppingListItem> items = new ArrayList<>();

		public CheapListAppWidgetViewsFactory() {
			setUpFirebase();
		}

		private void setUpFirebase() {
			DatabaseReference dbRefForShoppingList = getDBRefForShoppingList();
			if (dbRefForShoppingList != null) {
				dbRefForShoppingList.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<Map<String, ShoppingListItem>> genericTypeIndicator =
								new GenericTypeIndicator<Map<String, ShoppingListItem>>() {};
						Map<String, ShoppingListItem> itemsMap = dataSnapshot.getValue(
								genericTypeIndicator);
						items.clear();
						if (itemsMap != null) {
							items.addAll(itemsMap.values());
							sortByCheckedState();
							updateWidget();
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("Widget firebase cancel for shopping list %s", databaseError);
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

		private DatabaseReference getDBRefForShoppingList() {
			FirebaseAuth auth = FirebaseAuth.getInstance();
			FirebaseUser currentUser = auth.getCurrentUser();
			if (currentUser != null) {
				return FirebaseDatabase.getInstance().getReference().child("userData").child(
						currentUser.getUid()).child("shoppingList");
			}
			return null;
		}

		public void updateWidget() {
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
				row.setTextViewText(R.id.widgetItemName, items.get(position).name);
				row.setTextViewText(R.id.widgetItemPrice,
						String.format("%s %s", items.get(position).price,
								items.get(position).currency));

				Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
				Bundle extras = new Bundle();
				extras.putSerializable(DETAIL_ITEM, items.get(position));
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
