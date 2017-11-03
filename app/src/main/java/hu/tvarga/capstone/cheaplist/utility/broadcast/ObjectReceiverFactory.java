package hu.tvarga.capstone.cheaplist.utility.broadcast;

import javax.inject.Inject;

/**
 * This factory should help make usages of a object broadcast receiver easier to mock and test.
 * Also the factory is used in the {@link ObjectReceiver} tests so no need to create its
 * own test class as it is already covered.
 */
public class ObjectReceiverFactory {

	private LocalBroadcastManagerWrapper localBroadcastManagerWrapper;

	@Inject
	ObjectReceiverFactory(LocalBroadcastManagerWrapper localBroadcastManagerWrapper) {
		this.localBroadcastManagerWrapper = localBroadcastManagerWrapper;
	}

	public <T> ObjectReceiver<T> get(ObjectListener<T> broadcastListener, Class<T> clazz) {
		return new ObjectReceiver<>(localBroadcastManagerWrapper, broadcastListener, clazz);
	}

	public <T> ObjectReceiver<T> get(ObjectListener<T> broadcastListener, Class<T> clazz,
			boolean unregisterAfterReceivingBroadcast) {
		return new ObjectReceiver<>(localBroadcastManagerWrapper, broadcastListener, clazz,
				unregisterAfterReceivingBroadcast);
	}

}
