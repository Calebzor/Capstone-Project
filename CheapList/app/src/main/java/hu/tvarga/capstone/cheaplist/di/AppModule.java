package hu.tvarga.capstone.cheaplist.di;

import android.content.Context;
import android.os.Handler;

import dagger.Module;
import dagger.Provides;
import hu.tvarga.capstone.cheaplist.CheapListApp;
import hu.tvarga.capstone.cheaplist.di.androidinjectors.ActivityInjectorModule;
import hu.tvarga.capstone.cheaplist.di.androidinjectors.FragmentInjectorModule;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@Module(includes = {ActivityInjectorModule.class, FragmentInjectorModule.class})
@ApplicationScope
public class AppModule {

	private final CheapListApp application;

	public AppModule(CheapListApp app) {
		application = app;
	}

	@Provides
	Context provideContext() {
		return application.getApplicationContext();
	}

	@Provides
	Handler provideMainHandler() {
		return new Handler(application.getMainLooper());
	}

}
