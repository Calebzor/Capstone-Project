package hu.tvarga.capstone.cheaplist.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.di.DaggerWidgetServiceComponent;
import hu.tvarga.capstone.cheaplist.di.androidinjectors.ServiceModule;
import timber.log.Timber;

public class WidgetService extends RemoteViewsService {

	@Inject
	ShoppingListManager shoppingListManager;

	@Override
	public void onCreate() {
		super.onCreate();
		DaggerWidgetServiceComponent.builder().serviceModule(new ServiceModule(this)).build()
				.inject(this);
	}

	@Override
	public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new CheapListAppWidgetViewsFactory(getApplicationContext());
	}

	public class CheapListAppWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

		private Context context;

		public CheapListAppWidgetViewsFactory(Context applicationContext) {
			context = applicationContext;
		}

		@Override
		public void onCreate() {
			Timber.d("CheapListAppWidgetViewsFactory#onCreate");
			//			loadData(true);
		}

		@Override
		public void onDataSetChanged() {
			Timber.d("CheapListAppWidgetViewsFactory#onDataSetChanged");
			//			loadData(false);
		}

		@Override
		public void onDestroy() {
			Timber.d("CheapListAppWidgetViewsFactory#onDestroy");
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews row = new RemoteViews(context.getPackageName(),
					R.layout.widget_shopping_list_item);
			//			if (favoritesIndex == -1 || recepyWithIngredientAndSteps == null) {
			//				row.setViewVisibility(R.id.listItemContainer, GONE);
			//				row.setViewVisibility(R.id.defaultMessage, VISIBLE);
			//				return row;
			//			}
			//			row.setViewVisibility(R.id.listItemContainer, VISIBLE);
			//			row.setViewVisibility(R.id.defaultMessage, GONE);
			//			row.setTextViewText(R.id.text, recepyWithIngredientAndSteps.name);
			//			StringBuilder sb = new StringBuilder("");
			//			for (Ingredient ingredient : recepyWithIngredientAndSteps.ingredients) {
			//				if (ingredient.ingredient != null) {
			//					sb.append(ingredient.ingredient).append("\n");
			//				}
			//				if (ingredient.quantity != null && ingredient.measure != null) {
			//					sb.append(ingredient.quantity).append(" ").append(ingredient.measure);
			//				}
			//			}
			//			String ingredient = sb.toString();
			//			row.setTextViewText(R.id.ingredients, ingredient);

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

		//		public void loadData(final boolean shouldBroadcastUpdate) {
		//			SharedPreferences sharedPreferences = Preferences.getSharedPreferences(context);
		//			favoritesIndex = sharedPreferences.getInt(FAVORITE_RECEPY_INDEX, -1);
		//			runInBackgroundThread(new Runnable() {
		//				@Override
		//				public void run() {
		//					recepyWithIngredientAndSteps =
		//							dbFactory.getDb().recepyWithIngredientsAndStepsDao()
		//									.loadRecepyWithIngredientsAndSteps(favoritesIndex);
		//					if (shouldBroadcastUpdate) {
		//						Intent dataUpdatedIntent = new Intent(ACTION_UPDATE);
		//						context.sendBroadcast(dataUpdatedIntent);
		//					}
		//				}
		//			});
		//
		//		}
	}
}
