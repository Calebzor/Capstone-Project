package hu.tvarga.capstone.cheaplist.business.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import timber.log.Timber;

public class LocalBroadcastReceiver extends BroadcastReceiver {

	protected LocalBroadcastManagerWrapper localBroadcastManagerWrapper;

	private final BroadcastListener broadcastListener;

	protected final IntentFilter intentFilter;

	public LocalBroadcastReceiver(LocalBroadcastManagerWrapper localBroadcastManagerWrapper,
			BroadcastListener broadcastListener, String... senders) {
		this.localBroadcastManagerWrapper = localBroadcastManagerWrapper;
		this.broadcastListener = broadcastListener;

		intentFilter = localBroadcastManagerWrapper.getNewIntentFilter();
		for (String sender : senders) {
			intentFilter.addAction(sender);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Timber.d("LocalBroadcast action", intent.getAction());
		if (Broadcast.ERROR == intent.getSerializableExtra(Broadcast.RESULT_TYPE)) {
			handleLocalBroadcastError(intent);
		}
		else if (Broadcast.CANCELLED == intent.getSerializableExtra(Broadcast.RESULT_TYPE)) {
			handleLocalBroadcastCancel(intent);
		}
		else {
			handleLocalBroadcastSuccess(intent);
		}
	}

	protected void handleLocalBroadcastSuccess(Intent intent) {
		broadcastListener.onReceive(intent.getAction(), intent.getExtras());
	}

	protected void handleLocalBroadcastError(Intent intent) {
		broadcastListener.onFailure(intent.getAction(),
				intent.getSerializableExtra(Broadcast.ERROR_TYPE),
				(Throwable) intent.getSerializableExtra(Broadcast.RESULT), intent.getExtras());
	}

	protected void handleLocalBroadcastCancel(Intent intent) {
		broadcastListener.onCancelled(intent.getAction(),
				intent.getSerializableExtra(Broadcast.CANCELLED_TYPE));
	}
}
