package hu.tvarga.cheaplist.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.cheaplist.ui.detail.DetailFragment;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListFragment;
import hu.tvarga.cheaplist.utility.eventbus.EventBusBuffer;

import static hu.tvarga.cheaplist.utility.SharedElementTransition.setTransitionName;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends AuthBaseActivity
		implements FragmentManager.OnBackStackChangedListener {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;

	@BindView(R.id.navigationView)
	NavigationView navigationView;

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
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		setUpToolbar();
		setUpNavigationView();
		FirebaseCrash.log("Activity created");
	}

	private void setUpToolbar() {
		final DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
		drawerArrowDrawable.setColor(ContextCompat.getColor(this, R.color.secondaryTextColor));

		toolbar.setNavigationIcon(drawerArrowDrawable);
		setSupportActionBar(toolbar);
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			shouldDisplayHomeUp();
		}
	}

	private void setUpNavigationView() {
		setUpNavigationHeader();

		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						DaggerFragment fragmentByTag;
						String fragmentTag;
						switch (item.getItemId()) {
							case R.id.sign_out_menu:
								drawerLayout.closeDrawer(navigationView);
								AuthUI.getInstance().signOut(MainActivity.this);
								return true;
							case R.id.shoppingListMenuItem:
								fragmentTag = ShoppingListFragment.FRAGMENT_TAG;
								fragmentByTag = getFragmentByTag(fragmentTag);
								if (fragmentByTag == null) {
									fragmentByTag = ShoppingListFragment.newInstance();
								}
								drawerLayout.closeDrawer(navigationView);
								item.setChecked(true);
								replaceFragment(fragmentByTag);
								return true;
							case R.id.compareMenuItem:
								fragmentTag = CompareFragment.FRAGMENT_TAG;
								fragmentByTag = getFragmentByTag(fragmentTag);
								if (fragmentByTag == null) {
									fragmentByTag = new CompareFragment();
								}
								drawerLayout.closeDrawer(navigationView);
								item.setChecked(true);
								replaceFragment(fragmentByTag);
								return true;
							default:

						}
						drawerLayout.closeDrawer(navigationView);
						return false;
					}
				});
	}

	private void setUpNavigationHeader() {
		View headerView = navigationView.getHeaderView(0);
		TextView userName = headerView.findViewById(
				R.id.navigation_drawer_account_information_display_name);
		TextView email = headerView.findViewById(R.id.navigation_drawer_account_information_email);
		FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
		if (currentUser == null) {
			headerView.setVisibility(View.GONE);
			email.setVisibility(View.GONE);
		}
		else {
			headerView.setVisibility(View.VISIBLE);
			userName.setText(currentUser.getDisplayName());
			email.setVisibility(View.VISIBLE);
			email.setText(currentUser.getEmail());
			AppCompatImageView userImage = headerView.findViewById(
					R.id.navigation_drawer_user_account_picture_profile);
			Glide.with(this).load(currentUser.getPhotoUrl()).apply(
					RequestOptions.circleCropTransform()).into(userImage);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			boolean drawer = isBackStackEmpty();
			if (drawer) {
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isBackStackEmpty() {
		return getSupportFragmentManager().getBackStackEntryCount() == 0;
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
		shouldDisplayHomeUp();
		getSupportFragmentManager().beginTransaction().addSharedElement(holder.image,
				getString(R.string.detailImageTransition)).replace(
				R.id.mainActivityFragmentContainer, details).addToBackStack(
				DetailFragment.FRAGMENT_TAG).commit();
	}

	@Override
	public void onBackStackChanged() {
		shouldDisplayHomeUp();
	}

	@SuppressLint("ObjectAnimatorBinding")
	public void shouldDisplayHomeUp() {
		boolean drawer = isBackStackEmpty();
		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			if (drawer) {
				Menu menu = navigationView.getMenu();
				MenuItem item = menu.findItem(R.id.compareMenuItem);
				item.setChecked(true);
			}
			supportActionBar.setDisplayShowHomeEnabled(drawer);
		}
		ObjectAnimator.ofFloat(toolbar.getNavigationIcon(), "progress", drawer ? 0 : 1).start();
	}

	@Override
	public boolean onSupportNavigateUp() {
		getSupportFragmentManager().popBackStack();
		return true;
	}

	public interface SearchHandler {

		void setOnQueryTextListener(SearchView searchView);
	}
}
