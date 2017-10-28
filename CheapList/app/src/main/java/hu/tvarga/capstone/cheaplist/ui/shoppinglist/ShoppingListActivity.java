package hu.tvarga.capstone.cheaplist.ui.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.AdShowingActivity;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailFragment;

import static hu.tvarga.capstone.cheaplist.R.bool.multipane;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_FRAGMENT_INSTANCE_KEY;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.IMAGE_TRANSITION_NAME;

public class ShoppingListActivity extends AdShowingActivity
		implements ShoppingListFragment.ShoppingListItemClickAction {

	public static final String SHOPPING_LIST_FRAGMENT_INSTANCE_KEY =
			"SHOPPING_LIST_FRAGMENT_INSTANCE_KEY";

	private ShoppingListItem currentShoppingListDetailItem;
	private DetailFragment detailFragment;
	private ShoppingListFragment shoppingListFragment;
	private String transitionName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);
		ButterKnife.bind(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(DETAIL_ITEM)) {
			currentShoppingListDetailItem = (ShoppingListItem) extras.getSerializable(DETAIL_ITEM);
		}
		if (extras != null && extras.containsKey(IMAGE_TRANSITION_NAME)) {
			transitionName = extras.getString(IMAGE_TRANSITION_NAME);
		}

		if (savedInstanceState != null) {
			currentShoppingListDetailItem = (ShoppingListItem) savedInstanceState.getSerializable(
					DETAIL_ITEM);
			transitionName = savedInstanceState.getString(IMAGE_TRANSITION_NAME);
			detailFragment = (DetailFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, DETAIL_FRAGMENT_INSTANCE_KEY);
			shoppingListFragment = (ShoppingListFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, SHOPPING_LIST_FRAGMENT_INSTANCE_KEY);
		}

		if (detailFragment == null) {
			detailFragment = DetailFragment.newInstance(currentShoppingListDetailItem);
		}
		if (shoppingListFragment == null) {
			shoppingListFragment = new ShoppingListFragment();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		if (isMultiPane() && detailFragment != null) {
			detailFragment.setImageTransitionName(transitionName);
			fragmentManager.beginTransaction().replace(R.id.endPane, detailFragment).commit();
		}
		fragmentManager.beginTransaction().replace(R.id.startPane, shoppingListFragment).commit();
	}

	private boolean isMultiPane() {return getResources().getBoolean(multipane);}

	public boolean hasDetailFragment() {
		return detailFragment != null;
	}

	public void addDetailFragment(ShoppingListItem shoppingListItem) {
		if (isMultiPane()) {
			currentShoppingListDetailItem = shoppingListItem;
			detailFragment = DetailFragment.newInstance(currentShoppingListDetailItem);
			getSupportFragmentManager().beginTransaction().replace(R.id.endPane, detailFragment)
					.commit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (isMultiPane()) {
			outState.putSerializable(DETAIL_ITEM, currentShoppingListDetailItem);
			outState.putString(IMAGE_TRANSITION_NAME, transitionName);
			if (detailFragment != null) {
				getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_INSTANCE_KEY,
						detailFragment);
			}
		}
		getSupportFragmentManager().putFragment(outState, SHOPPING_LIST_FRAGMENT_INSTANCE_KEY,
				shoppingListFragment);
	}

	@Override
	public void onShoppingListItemClick(ShoppingListItem item) {
		if (getResources().getBoolean(multipane)) {
			detailFragment.setItemFromArgument(item);
			detailFragment.loadData();
		}
		else {
			Class<?> targetActivity = DetailActivity.class;
			Intent intent = new Intent(this, targetActivity);
			intent.putExtra(DETAIL_ITEM, item);
			startActivity(intent);
		}
	}
}
