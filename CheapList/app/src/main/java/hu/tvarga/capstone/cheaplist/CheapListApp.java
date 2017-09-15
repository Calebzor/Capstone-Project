package hu.tvarga.capstone.cheaplist;

import android.app.Application;

import timber.log.Timber;

public class CheapListApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initializeApplication();
	}

	private void initializeApplication() {
		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
	}
}
