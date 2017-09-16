package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.capstone.cheaplist.di.scopes.ActivityScope;
import hu.tvarga.capstone.cheaplist.ui.CompareActivity;

@Module
public interface ActivityInjectorModule {

	@ActivityScope
	@ContributesAndroidInjector
	CompareActivity contributesCompareActivityInjector();

}