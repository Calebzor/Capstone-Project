package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.tvarga.capstone.cheaplist.di.scopes.ActivityScope;
import hu.tvarga.capstone.cheaplist.ui.MainActivity;
import hu.tvarga.capstone.cheaplist.ui.compare.CompareActivity;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListActivity;

@Module(includes = {PresenterModule.class})
public interface ActivityInjectorModule {

	@ActivityScope
	@ContributesAndroidInjector
	MainActivity contributesMainActivityInjector();

	@ActivityScope
	@ContributesAndroidInjector
	CompareActivity contributesCompareActivityInjector();

	@ActivityScope
	@ContributesAndroidInjector
	DetailActivity contributesDetailActivityInjector();

	@ActivityScope
	@ContributesAndroidInjector
	ShoppingListActivity contributesShoppingListActivityInjector();
}