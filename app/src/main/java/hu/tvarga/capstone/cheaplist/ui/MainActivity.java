package hu.tvarga.capstone.cheaplist.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailFragment;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends AuthBaseActivity {

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
	}

	public void openDetailView(ShoppingListItem item, ImageBasedListItemHolder holder) {
		DetailFragment details = DetailFragment.newInstance(item);
		getSupportFragmentManager().beginTransaction().addSharedElement(holder.image, getString(R.string.detailImageTransition)).replace(
				R.id.mainActivityFragmentContainer, details).addToBackStack(DetailFragment.FRAGMENT_TAG).commit();
	}
}
