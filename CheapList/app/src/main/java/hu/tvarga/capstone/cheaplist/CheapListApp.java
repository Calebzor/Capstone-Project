package hu.tvarga.capstone.cheaplist;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

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
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		firebaseDatabase.setPersistenceEnabled(true);
	}
}
