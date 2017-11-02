package hu.tvarga.capstone.cheaplist.business.compare.settings;

import android.support.v7.widget.RecyclerView;

import hu.tvarga.capstone.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;

public interface CompareSettingsContract {

	interface Presenter {

		void onStart(CompareSettingsContract.View view);
		void onStop();
		RecyclerView.Adapter<CompareSettingsCategoryHolder> getCategoriesFilterForUserAdapter();
	}

	interface View {}
}
