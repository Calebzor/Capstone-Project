package hu.tvarga.capstone.cheaplist.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailFragment;
import hu.tvarga.capstone.cheaplist.utility.broadcast.BroadcastBuffer;

public class MainActivity extends AuthBaseActivity {

	@Inject
	BroadcastBuffer broadcastBuffer;

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

	@Override
	protected void onPause() {
		super.onPause();
		broadcastBuffer.startBuffering();
	}

	@Override
	public void onPostResume() {
		super.onPostResume();
		// After the activity completely finished onResume we do a replay of all the buffered
		// intents which could not be broadcast.
		broadcastBuffer.replayAllAndClearBuffer();
	}

	public void openDetailView(ShoppingListItem item, ImageBasedListItemHolder holder) {
		DetailFragment details = DetailFragment.newInstance(item);
		getSupportFragmentManager().beginTransaction().addSharedElement(holder.image,
				getString(R.string.detailImageTransition)).replace(
				R.id.mainActivityFragmentContainer, details).addToBackStack(
				DetailFragment.FRAGMENT_TAG).commit();
	}
}
