package hu.tvarga.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.cheaplist.di.scopes.ActivityScope;
import hu.tvarga.cheaplist.ui.MainActivity;
import hu.tvarga.cheaplist.ui.StartActivity;

@Module(includes = {PresenterModule.class})
public interface ActivityInjectorModule {

	@ActivityScope
	@ContributesAndroidInjector
	MainActivity contributesMainActivityInjector();

	@ActivityScope
	@ContributesAndroidInjector
	StartActivity contributesStartActivityInjector();
}