package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.capstone.cheaplist.di.scopes.ActivityScope;
import hu.tvarga.capstone.cheaplist.ui.compare.CompareActivity;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity;

@Module
public interface ActivityInjectorModule {

	@ActivityScope
	@ContributesAndroidInjector
	CompareActivity contributesCompareActivityInjector();

	@ActivityScope
	@ContributesAndroidInjector
	DetailActivity contributesDetailActivityInjector();

}