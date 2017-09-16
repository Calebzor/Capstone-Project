package hu.tvarga.capstone.cheaplist.ui.shoppinglist;

import android.os.Bundle;
import android.view.View;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.ui.AuthBaseActivity;

import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;

public class ShoppingListActivity extends AuthBaseActivity {

	@BindView(R.id.startPane)
	View startPane;

	@Nullable
	@BindView(R.id.endPane)
	View endPane;

	private String currentShoppingListDetailItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);
		ButterKnife.bind(this);

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(DETAIL_ITEM)) {
			currentShoppingListDetailItem = extras.getString(DETAIL_ITEM);
		}

		if (savedInstanceState != null) {
			currentShoppingListDetailItem = savedInstanceState.getString(DETAIL_ITEM);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.getString(DETAIL_ITEM, currentShoppingListDetailItem);
	}
}
