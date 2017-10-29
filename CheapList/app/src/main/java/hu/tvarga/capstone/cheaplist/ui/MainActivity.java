package hu.tvarga.capstone.cheaplist.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.broadcast.BroadcastBuffer;
import hu.tvarga.capstone.cheaplist.ui.compare.CompareFragment;

public class MainActivity extends AuthBaseActivity {

	public static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";
	private DaggerFragment currentFragment;

	@Inject
	BroadcastBuffer broadcastBuffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		if (savedInstanceState != null) {
			currentFragment = (DaggerFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, CURRENT_FRAGMENT);
		}

		if (currentFragment == null) {
			currentFragment = new CompareFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.mainActivityFragmentContainer,
					currentFragment).commit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, currentFragment);
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
}
