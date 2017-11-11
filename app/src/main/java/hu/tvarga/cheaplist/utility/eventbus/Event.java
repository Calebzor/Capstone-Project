package hu.tvarga.cheaplist.utility.eventbus;

import javax.inject.Inject;

import hu.tvarga.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
public class Event {

	private final EventBusBuffer eventBusBuffer;
	private final EventBusWrapper eventBusWrapper;

	@Inject
	public Event(EventBusBuffer eventBusBuffer, EventBusWrapper eventBusWrapper) {
		this.eventBusBuffer = eventBusBuffer;
		this.eventBusWrapper = eventBusWrapper;
	}

	public void post(Object object) {
		eventBusBuffer.buffer(object);
		eventBusWrapper.post(object);
	}

	public void register(Object object) {
		eventBusWrapper.register(object);
	}

	public void unregister(Object object) {
		eventBusWrapper.unregister(object);
	}
}
