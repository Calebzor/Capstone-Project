package hu.tvarga.capstone.cheaplist.business.compare.Settings;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.business.compare.CompareContract;

public class CompareSettingsPresenter implements CompareSettingsContract.Presenter {

	private final CompareContract.Presenter comparePresenter;
	private CompareSettingsContract.View view;

	@Inject
	public CompareSettingsPresenter(CompareContract.Presenter comparePresenter) {
		view = new DialogSettingsViewStub();
		this.comparePresenter = comparePresenter;

	}

	@Override
	public void onStart(CompareSettingsContract.View view) {
		this.view = view;
	}

	@Override
	public void onStop() {
		view = new DialogSettingsViewStub();
	}
}
