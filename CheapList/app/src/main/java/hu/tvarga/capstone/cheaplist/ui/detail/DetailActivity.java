package hu.tvarga.capstone.cheaplist.ui.detail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.ui.AuthBaseActivity;

public class DetailActivity extends AuthBaseActivity {

	public static final String DETAIL_ITEM = "DETAIL_ITEM";
	public static final String DETAIL_FRAGMENT_INSTANCE_KEY = "DETAIL_FRAGMENT_INSTANCE_KEY";

	private String currentShoppingListDetailItem;

	private DetailFragment detailFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		ButterKnife.bind(this);

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(DETAIL_ITEM)) {
			currentShoppingListDetailItem = extras.getString(DETAIL_ITEM);
		}

		if (savedInstanceState != null) {
			currentShoppingListDetailItem = savedInstanceState.getString(DETAIL_ITEM);
			detailFragment = (DetailFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, DETAIL_FRAGMENT_INSTANCE_KEY);
		}

		if (detailFragment == null) {
			detailFragment = DetailFragment.newInstance(currentShoppingListDetailItem);
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.startPane, detailFragment).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.getString(DETAIL_ITEM, currentShoppingListDetailItem);
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
