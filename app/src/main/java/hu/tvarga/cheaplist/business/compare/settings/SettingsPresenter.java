package hu.tvarga.cheaplist.business.compare.settings;

import javax.inject.Inject;

import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.dao.UserSetting;
import hu.tvarga.cheaplist.dao.UserSettingType;

public class SettingsPresenter implements SettingsContract.Presenter {

	private final UserService userService;

	@Inject
	public SettingsPresenter(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void onSettingsClicked(UserSetting userSetting) {
		if (userSetting.userSettingType.equals(UserSettingType.CHECKBOX)) {
			Boolean value = (Boolean) userSetting.value;
			userSetting.value = !value;
		}
		userService.setUserSetting(userSetting);
	}

}
