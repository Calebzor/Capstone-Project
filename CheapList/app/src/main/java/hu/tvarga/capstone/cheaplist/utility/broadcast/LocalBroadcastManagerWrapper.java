package hu.tvarga.capstone.cheaplist.utility.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import timber.log.Timber;

@ApplicationScope
public class LocalBroadcastManagerWrapper {

	private Context context;

	@Inject
	public LocalBroadcastManagerWrapper(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * Sends an intent via local broadcast. If there is no current activity in 'resumed' state, the
	 * intent is buffered in {@link BroadcastBuffer}.
	 *
	 * @param intent to be broadcasted
	 */
	void sendBroadcast(Intent intent) {
		boolean receiverExists = getLocalBroadcastManager().sendBroadcast(intent);
		if (!receiverExists) {
			Timber.d("No broadcast receiver registered for:", intent);
		}
	}

	void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		getLocalBroadcastManager().registerReceiver(receiver, filter);
	}

	void unregisterReceiver(BroadcastReceiver receiver) {
		getLocalBroadcastManager().unregisterReceiver(receiver);
	}

	// visible for test/mocking purposes
	private LocalBroadcastManager getLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(context);
	}

	/**
	 * IntentFilter.addAction() will throw errors if the IntentFilter is not mocked. Therefore we
	 * need the possibility to inject a mock IntentFilter even if we are in the constructor of an
	 * object, and the best place for this is in this class as it is basically a wrapper anyway
	 *
	 * @return A new IntentFilter
	 */
	IntentFilter getNewIntentFilter() {
		return new IntentFilter();
	}
}
