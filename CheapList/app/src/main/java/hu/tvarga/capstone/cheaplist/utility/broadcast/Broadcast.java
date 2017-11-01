package hu.tvarga.capstone.cheaplist.utility.broadcast;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Type;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
public class Broadcast {

	private static final String PREFIX = Broadcast.class.getCanonicalName();

	static final String RESULT_TYPE = PREFIX + ".RESULT_TYPE";
	static final String RESULT = PREFIX + ".RESULT";
	static final String ERROR = PREFIX + ".ERROR";
	static final String ERROR_TYPE = PREFIX + ".ERROR_TYPE";
	static final String CANCELLED = PREFIX + ".CANCELLED";
	static final String CANCELLED_TYPE = PREFIX + ".CANCELLED_TYPE";

	private LocalBroadcastManagerWrapper localBroadcastManager;
	private BroadcastBuffer broadcastBuffer;

	@Inject
	Broadcast(LocalBroadcastManagerWrapper localBroadcastManager, BroadcastBuffer broadcastBuffer) {
		this.localBroadcastManager = localBroadcastManager;
		this.broadcastBuffer = broadcastBuffer;
	}

	/**
	 * This function will broadcast your object using the class as action for identifying purposes
	 *
	 * @param object The object you wish to broadcast
	 */
	public void sendObject(@NonNull Serializable object) {
		Type type = object.getClass();
		Intent intent = new Intent(type.toString());
		intent.putExtra(RESULT, object);
		sendAndBufferBroadcast(intent);
	}

	/**
	 * Send a broadcast telling that the retrieval of the object was cancelled
	 *
	 * @param clazz The class of the object you tried to retrieve
	 */
	public void sendObjectCancelled(@NonNull Class clazz) {
		sendObjectCancelled(clazz.toString());
	}

	/**
	 * Send a throwable received while obtaining a certain object
	 *
	 * @param clazz     The class of the object you tried to retrieve
	 * @param throwable The reason why the retrieval failed
	 */
	public void sendObjectFailure(@NonNull Class clazz, Throwable throwable) {
		sendObjectFailure(clazz.toString(), throwable);
	}

	private void sendObjectCancelled(@NonNull String type) {
		Intent intent = new Intent(type);
		intent.putExtra(RESULT_TYPE, CANCELLED);
		sendAndBufferBroadcast(intent);
	}

	private void sendObjectFailure(@NonNull String type, Throwable throwable) {
		Intent intent = new Intent(type);
		intent.putExtra(RESULT_TYPE, ERROR);
		intent.putExtra(RESULT, throwable);
		sendAndBufferBroadcast(intent);
	}

	private void sendAndBufferBroadcast(Intent intent) {
		// Buffer the intent for later replay
		broadcastBuffer.buffer(intent);

		// Send out the broadcast
		localBroadcastManager.sendBroadcast(intent);
	}

}
