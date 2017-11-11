package hu.tvarga.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.cheaplist.di.scopes.FragmentScope;
import hu.tvarga.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsDialog;
import hu.tvarga.cheaplist.ui.detail.DetailFragment;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListFragment;

@Module(includes = {PresenterModule.class})
public interface FragmentInjectorModule {

	@FragmentScope
	@ContributesAndroidInjector
	CompareFragment contributesCompareFragmentInjector();

	@FragmentScope
	@ContributesAndroidInjector
	DetailFragment contributesDetailFragmentInjector();

	@FragmentScope
	@ContributesAndroidInjector
	ShoppingListFragment contributesShoppingListFragmentInjector();

	@FragmentScope
	@ContributesAndroidInjector
	CompareSettingsDialog contributesCompareSettingsDialogInjector();
}
