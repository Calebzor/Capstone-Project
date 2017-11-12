package hu.tvarga.cheaplist.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.google.firebase.crash.FirebaseCrash;

import javax.inject.Inject;

import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.cheaplist.ui.detail.DetailFragment;
import hu.tvarga.cheaplist.utility.eventbus.EventBusBuffer;

import static hu.tvarga.cheaplist.utility.SharedElementTransition.setTransitionName;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends AuthBaseActivity {

	@Inject
	EventBusBuffer eventBusBuffer;

	private SearchView searchView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		if (savedInstanceState == null) {
			CompareFragment compareFragment = new CompareFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.mainActivityFragmentContainer,
					compareFragment, compareFragment.getClass().getName()).commit();
		}
		handleIntent(getIntent());
		FirebaseCrash.log("Activity created");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			search(query);
		}
	}

	private void search(String query) {
		if (searchView != null) {
			searchView.setQuery(query, true);
		}
	}

	@Override
	protected void setUpSearchView(Menu menu) {
		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			searchView = (SearchView) menu.findItem(R.id.searchMenuItem).getActionView();
			// Assumes current activity is the searchable activity
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			// Do not iconify the widget; expand it by default
			searchView.setIconifiedByDefault(false);
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(
					CompareFragment.FRAGMENT_TAG);
			if (fragment != null && fragment instanceof SearchHandler && fragment.isVisible()) {
				SearchHandler searchViewHandler = (SearchHandler) fragment;
				searchViewHandler.setOnQueryTextListener(searchView);
			}
		}
	}

	@Override
	protected void onPause() {
		eventBusBuffer.startBuffering();
		super.onPause();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		eventBusBuffer.replayAndClearBuffer();
	}

	public void openDetailView(ShoppingListItem item, ImageBasedListItemHolder holder) {
		DetailFragment details = DetailFragment.newInstance(item);
		// somehow sometimes the holder.image looses the transition name, so we set it here again
		setTransitionName(holder.image, item.merchant, item.id);
		getSupportFragmentManager().beginTransaction().addSharedElement(holder.image,
				getString(R.string.detailImageTransition)).replace(
				R.id.mainActivityFragmentContainer, details).addToBackStack(
				DetailFragment.FRAGMENT_TAG).commit();
	}

	public interface SearchHandler {

		void setOnQueryTextListener(SearchView searchView);
	}
}
