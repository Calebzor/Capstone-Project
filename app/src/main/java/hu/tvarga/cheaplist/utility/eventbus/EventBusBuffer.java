package hu.tvarga.cheaplist.utility.eventbus;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import hu.tvarga.cheaplist.di.scopes.ApplicationScope;
import timber.log.Timber;

@ApplicationScope
public class EventBusBuffer {

	private final EventBusWrapper eventBusWrapper;
	private Queue<Object> buffer = new LinkedList<>();

	private boolean buffering = false;

	@Inject
	EventBusBuffer(EventBusWrapper eventBusWrapper) {
		this.eventBusWrapper = eventBusWrapper;
	}

	Queue<Object> getBuffer() {
		return buffer;
	}

	boolean isBuffering() {
		return buffering;
	}

	void buffer(Object object) {
		if (buffering) {
			Timber.v("Buffering object for later replay", object);
			buffer.add(object);
		}
		else {
			Timber.v("Not buffering currently, object not saved:", object);
		}
	}

	public void startBuffering() {
		buffering = true;
	}

	public void replayAndClearBuffer() {
		Timber.d("Replaying # of '", buffer.size(), "' objects via event bus.");
		buffering = false;
		while (buffer.peek() != null) {
			Object object = buffer.remove();
			Timber.v("Replaying object:", object);
			eventBusWrapper.post(object);
		}
	}
}
