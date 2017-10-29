package hu.tvarga.capstone.cheaplist.business.broadcast;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Our custom implementation for a listener for our local broadcasts. See {@link Broadcast}
 */
public interface BroadcastListener {

	/**
	 * Called after the normal broadcast methods.
	 *
	 * @param sender The sending instance (generally a name of the sending class)
	 * @param extras Extras sent with the broadcast
	 */
	void onReceive(String sender, Bundle extras);

	/**
	 * Called if broadcast send methods containing an Error/Throwable are used. Example:
	 * sendFailure(String sender, Serializable type, Throwable throwable)
	 *
	 * @param sender    The sending instance (generally a name of the sending class)
	 * @param errorType Type of the error
	 * @param throwable The actual error as a throwable
	 * @param extras    Extras sent with the broadcast
	 */
	void onFailure(String sender, Serializable errorType, Throwable throwable, Bundle extras);

	void onCancelled(String sender, Serializable errorType);
}
