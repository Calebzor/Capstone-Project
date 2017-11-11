package hu.tvarga.cheaplist.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.firebase.crash.FirebaseCrash;

import javax.inject.Inject;

import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.cheaplist.ui.detail.DetailFragment;
import hu.tvarga.cheaplist.utility.eventbus.EventBusBuffer;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends AuthBaseActivity {

	@Inject
	EventBusBuffer eventBusBuffer;

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
		FirebaseCrash.log("Activity created");
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
		getSupportFragmentManager().beginTransaction().addSharedElement(holder.image,
				getString(R.string.detailImageTransition)).replace(
				R.id.mainActivityFragmentContainer, details).addToBackStack(
				DetailFragment.FRAGMENT_TAG).commit();
	}
}
