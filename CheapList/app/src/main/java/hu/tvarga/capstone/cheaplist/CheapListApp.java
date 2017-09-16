package hu.tvarga.capstone.cheaplist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import hu.tvarga.capstone.cheaplist.di.AppModule;
import hu.tvarga.capstone.cheaplist.di.DaggerAppComponent;
import timber.log.Timber;

public class CheapListApp extends Application implements HasActivityInjector {

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

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
		DaggerAppComponent.builder().appModule(new AppModule(this)).build().inject(this);
	}

	public static CheapListApp get(Context context) {
		return (CheapListApp) context.getApplicationContext();
	}

	@Override
	public DispatchingAndroidInjector<Activity> activityInjector() {
		return dispatchingAndroidInjector;
	}
}
