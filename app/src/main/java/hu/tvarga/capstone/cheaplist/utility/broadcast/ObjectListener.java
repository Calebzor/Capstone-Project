package hu.tvarga.capstone.cheaplist.utility.broadcast;

public interface ObjectListener<T> {

	/**
	 * Called after the normal broadcast methods.
	 *
	 * @param object The received object
	 */
	void onReceive(T object);

	/**
	 * Called if broadcast send methods containing an Error/Throwable are used. Example:
	 * sendObjectFailure(..)
	 *
	 * @param throwable The actual error as a throwable
	 */
	void onFailure(Throwable throwable);

	/**
	 * Called when the fetching of the object is cancelled
	 */
	void onCancelled();

}
