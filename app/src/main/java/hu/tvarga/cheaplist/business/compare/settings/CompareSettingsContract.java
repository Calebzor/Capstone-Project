package hu.tvarga.cheaplist.business.compare.settings;

import android.support.v7.widget.RecyclerView;

import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;

public interface CompareSettingsContract {

	interface Presenter {

		void onStart();
		void onStop();
		RecyclerView.Adapter<CompareSettingsCategoryHolder> getCategoriesFilterForUserAdapter();
	}

	interface View {}
}
