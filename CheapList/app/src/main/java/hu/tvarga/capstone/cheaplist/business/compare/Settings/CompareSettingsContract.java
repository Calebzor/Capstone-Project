package hu.tvarga.capstone.cheaplist.business.compare.Settings;

public interface CompareSettingsContract {

	interface Presenter {

		void onStart(CompareSettingsContract.View view);
		void onStop();
	}

	interface View {}
}
