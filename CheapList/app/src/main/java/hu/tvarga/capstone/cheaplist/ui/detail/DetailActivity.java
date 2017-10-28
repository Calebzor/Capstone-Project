package hu.tvarga.capstone.cheaplist.ui.detail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.AdShowingActivity;

public class DetailActivity extends AdShowingActivity {

	public static final String DETAIL_ITEM = "DETAIL_ITEM";
	public static final String IMAGE_TRANSITION_NAME = "IMAGE_TRANSITION_NAME";
	public static final String DETAIL_FRAGMENT_INSTANCE_KEY = "DETAIL_FRAGMENT_INSTANCE_KEY";

	private ShoppingListItem currentShoppingListDetailItem;
	private String transitionName;

	private DetailFragment detailFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		ButterKnife.bind(this);

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(DETAIL_ITEM)) {
			currentShoppingListDetailItem = (ShoppingListItem) extras.getSerializable(DETAIL_ITEM);
		}
		if (extras.containsKey(IMAGE_TRANSITION_NAME)) {
			transitionName = extras.getString(IMAGE_TRANSITION_NAME);
		}

		if (savedInstanceState != null) {
			currentShoppingListDetailItem = (ShoppingListItem) savedInstanceState.getSerializable(
					DETAIL_ITEM);
			transitionName = savedInstanceState.getString(IMAGE_TRANSITION_NAME);
			detailFragment = (DetailFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, DETAIL_FRAGMENT_INSTANCE_KEY);
		}

		if (detailFragment == null) {
			detailFragment = DetailFragment.newInstance(currentShoppingListDetailItem);
		}

		detailFragment.setImageTransitionName(transitionName);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.startPane, detailFragment).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(DETAIL_ITEM, currentShoppingListDetailItem);
		outState.putString(IMAGE_TRANSITION_NAME, transitionName);
		getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_INSTANCE_KEY,
				detailFragment);
	}

	@Override
	public void onPause() {
		if (isFinishing()) {
			getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
		}
		super.onPause();
	}

}
