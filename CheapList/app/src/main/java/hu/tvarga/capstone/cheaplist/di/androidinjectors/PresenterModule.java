package hu.tvarga.capstone.cheaplist.di.androidinjectors;

import dagger.Binds;
import dagger.Module;
import hu.tvarga.capstone.cheaplist.business.compare.CompareContract;
import hu.tvarga.capstone.cheaplist.business.compare.ComparePresenter;

@Module
public abstract class PresenterModule {

	@Binds
	public abstract CompareContract.Presenter bindComparePresenter(
			ComparePresenter comparePresenter);
}
