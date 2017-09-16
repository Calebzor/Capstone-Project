package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.capstone.cheaplist.di.scopes.FragmentScope;
import hu.tvarga.capstone.cheaplist.ui.CompareFragment;

@Module
public interface FragmentInjectorModule {

	@FragmentScope
	@ContributesAndroidInjector
	CompareFragment contributesCompareFragmentInjector();

}
