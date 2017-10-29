package hu.tvarga.capstone.cheaplist.business.broadcast;

import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import timber.log.Timber;

/**
 * Allows to buffer intents at broadcast time. The intents are stored in memory in a queue and can
 * be replayed at a later point in the order they where buffered.
 */
@ApplicationScope
public class BroadcastBuffer {

	private LocalBroadcastManagerWrapper localBroadcastManager;

	private Queue<Intent> bufferedBroadcasts = new LinkedList<>();

	private boolean buffering = false;

	@Inject
	BroadcastBuffer(LocalBroadcastManagerWrapper localBroadcastManager) {
		this.localBroadcastManager = localBroadcastManager;
	}

	Queue<Intent> getBufferedBroadcasts() {
		return bufferedBroadcasts;
	}

	boolean isBuffering() {
		return buffering;
	}

	/**
	 * Buffers an intent if the {@link BroadcastBuffer} is set to do so.
	 *
	 * @param intent the intent which is buffered for later replay
	 */
	void buffer(Intent intent) {
		if (buffering) {
			Timber.v("Buffering intent for later replay: ", intent);
			this.bufferedBroadcasts.add(intent);
		}
		else {
			Timber.v("Not buffering currently, intent not save: ", intent);
		}
	}

	/**
	 * Starts buffering intents sent via local broadcast.
	 */
	public void startBuffering() {
		buffering = true;
	}

	/**
	 * Replays all buffered intents via the standard localBroadcastManager in the order they where
	 * buffered.
	 */
	public void replayAllAndClearBuffer() {
		Timber.d("Replaying # of '", bufferedBroadcasts.size(), "' intents via broadcast.");
		buffering = false;
		while (bufferedBroadcasts.peek() != null) {
			Intent intent = bufferedBroadcasts.remove();
			Timber.v("Replaying intent: ", intent);
			localBroadcastManager.sendBroadcast(intent);
		}
	}

}
