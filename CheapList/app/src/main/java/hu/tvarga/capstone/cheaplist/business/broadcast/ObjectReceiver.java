package hu.tvarga.capstone.cheaplist.business.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.Serializable;

import timber.log.Timber;

public class ObjectReceiver<T> extends LocalBroadcastReceiver {

	private final ObjectListener<T> broadcastListener;

	private final String type;

	private final boolean unregisterAfterReceivingBroadcast;

	private boolean isRegistered;

	public ObjectReceiver(LocalBroadcastManagerWrapper localBroadcastManagerWrapper,
			@NonNull ObjectListener<T> broadcastListener, @NonNull Class<T> clazz) {
		this(localBroadcastManagerWrapper, broadcastListener, clazz.toString(), false);
	}

	public ObjectReceiver(LocalBroadcastManagerWrapper localBroadcastManagerWrapper,
			@NonNull ObjectListener<T> broadcastListener, @NonNull Class<T> clazz,
			boolean unregisterAfterReceivingBroadcast) {
		this(localBroadcastManagerWrapper, broadcastListener, clazz.toString(),
				unregisterAfterReceivingBroadcast);
	}

	private ObjectReceiver(LocalBroadcastManagerWrapper localBroadcastManagerWrapper,
			@NonNull ObjectListener<T> broadcastListener, @NonNull String type,
			boolean unregisterAfterReceivingBroadcast) {
		super(localBroadcastManagerWrapper, null, type);
		this.type = type;
		this.broadcastListener = broadcastListener;
		this.unregisterAfterReceivingBroadcast = unregisterAfterReceivingBroadcast;
	}

	public void unregister() {
		if (isRegistered) {
			isRegistered = false;
			localBroadcastManagerWrapper.unregisterReceiver(this);
		}
	}

	public void register() {
		if (!isRegistered) {
			isRegistered = true;
			localBroadcastManagerWrapper.registerReceiver(this, intentFilter);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (unregisterAfterReceivingBroadcast) {
			unregister();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleLocalBroadcastSuccess(Intent intent) {
		Serializable result = intent.getSerializableExtra(Broadcast.RESULT);
		if (result != null) {
			if (type.equals(result.getClass().toString())) {
				broadcastListener.onReceive((T) result);
			}
			else {
				Timber.e("Object not recognized, returning some error");
				broadcastListener.onFailure(new Throwable());
			}
		}
		else {
			Timber.e("Object null, returning some error");
			broadcastListener.onFailure(new Throwable());
		}
	}

	@Override
	protected void handleLocalBroadcastError(Intent intent) {
		broadcastListener.onFailure((Throwable) intent.getSerializableExtra(Broadcast.RESULT));
	}

	@Override
	protected void handleLocalBroadcastCancel(Intent intent) {
		broadcastListener.onCancelled();
	}
}
