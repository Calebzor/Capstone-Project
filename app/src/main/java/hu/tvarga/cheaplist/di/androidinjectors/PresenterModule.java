package hu.tvarga.cheaplist.di.androidinjectors;

import dagger.Binds;
import dagger.Module;
import hu.tvarga.cheaplist.business.compare.CompareContract;
import hu.tvarga.cheaplist.business.compare.ComparePresenter;
import hu.tvarga.cheaplist.business.compare.settings.CompareSettingsContract;
import hu.tvarga.cheaplist.business.compare.settings.CompareSettingsPresenter;
import hu.tvarga.cheaplist.business.compare.settings.SettingsContract;
import hu.tvarga.cheaplist.business.compare.settings.SettingsPresenter;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListContract;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListPresenter;
import hu.tvarga.cheaplist.business.itemdetail.DetailContract;
import hu.tvarga.cheaplist.business.itemdetail.DetailPresenter;

@Module
public abstract class PresenterModule {

	@Binds
	public abstract CompareContract.Presenter bindComparePresenter(
			ComparePresenter comparePresenter);

	@Binds
	public abstract DetailContract.Presenter bindDetailPresenter(DetailPresenter detailPresenter);

	@Binds
	public abstract CompareSettingsContract.Presenter bindCompareSettingsPresenter(
			CompareSettingsPresenter compareSettingsPresenter);

	@Binds
	public abstract ShoppingListContract.Presenter bindShoppingListPresenter(
			ShoppingListPresenter shoppingListPresenter);

	@Binds
	public abstract SettingsContract.Presenter bindSettingsPresenter(
			SettingsPresenter settingsPresenter);
}
