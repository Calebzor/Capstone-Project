package hu.tvarga.capstone.cheaplist.utility;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class EventBusWrapper {

	@Inject
	public EventBusWrapper() {
		// for dagger
	}

	public EventBus getDefault() {
		return EventBus.getDefault();
	}
}
