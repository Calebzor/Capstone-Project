package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Binds;
import dagger.Module;
import hu.tvarga.capstone.cheaplist.business.compare.CompareContract;
import hu.tvarga.capstone.cheaplist.business.compare.ComparePresenter;
import hu.tvarga.capstone.cheaplist.business.compare.settings.CompareSettingsContract;
import hu.tvarga.capstone.cheaplist.business.compare.settings.CompareSettingsPresenter;
import hu.tvarga.capstone.cheaplist.business.itemdetail.DetailContract;
import hu.tvarga.capstone.cheaplist.business.itemdetail.DetailPresenter;

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
}
