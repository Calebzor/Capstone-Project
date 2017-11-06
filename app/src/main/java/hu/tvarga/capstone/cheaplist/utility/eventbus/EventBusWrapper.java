package hu.tvarga.capstone.cheaplist.utility.eventbus;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
public class EventBusWrapper {

	@Inject
	public EventBusWrapper() {
		// for dagger
	}

	EventBus getDefault() {
		return EventBus.getDefault();
	}

	void post(Object object) {
		getDefault().post(object);
	}

	void register(Object object) {
		getDefault().register(object);
	}

	void unregister(Object object) {
		getDefault().unregister(object);
	}
}
