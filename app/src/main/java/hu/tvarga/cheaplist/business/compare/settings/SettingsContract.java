package hu.tvarga.cheaplist.business.compare.settings;

import hu.tvarga.cheaplist.dao.UserSetting;

public interface SettingsContract {

	interface Presenter {

		void onSettingsClicked(UserSetting userSetting);
	}

	interface View {

	}
}
