package hu.tvarga.cheaplist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.appevents.AppEventsLogger;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import hu.tvarga.cheaplist.di.AppModule;
import hu.tvarga.cheaplist.di.DaggerAppComponent;
import hu.tvarga.cheaplist.jobservices.CategoriesJobService;
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
		AppEventsLogger.activateApp(this);
		FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
		FirebaseFirestoreSettings settings =
				new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
		firebaseFirestore.setFirestoreSettings(settings);
		DaggerAppComponent.builder().appModule(new AppModule(this)).build().inject(this);
		setUpCategoriesJob();
	}

	private void setUpCategoriesJob() {
		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
		Job categoriesJob = dispatcher.newJobBuilder().setService(CategoriesJobService.class)
				.setTag(CategoriesJobService.class.getName()).setRecurring(true).setTrigger(
						Trigger.executionWindow(12 * 60 * 60, 24 * 60 * 60)).setReplaceCurrent(
						false).setConstraints(Constraint.ON_UNMETERED_NETWORK,
						Constraint.DEVICE_CHARGING).setLifetime(Lifetime.UNTIL_NEXT_BOOT).build();

		dispatcher.mustSchedule(categoriesJob);
	}

	public static CheapListApp get(Context context) {
		return (CheapListApp) context.getApplicationContext();
	}

	@Override
	public DispatchingAndroidInjector<Activity> activityInjector() {
		return dispatchingAndroidInjector;
	}
}
