package hu.tvarga.cheaplist.business.compare.settings;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.dao.UserSetting;
import hu.tvarga.cheaplist.dao.UserSettingType;

import static org.mockito.Mockito.verify;

public class SettingsPresenterTest extends MockitoJUnitTest {

	@Mock
	private UserService userService;

	@Captor
	private ArgumentCaptor<UserSetting> userSettingArgumentCaptor;

	private SettingsPresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new SettingsPresenter(userService);
	}

	@Test
	public void onSettingsClicked() throws Exception {
		UserSetting userSetting = new UserSetting();
		userSetting.userSettingType = UserSettingType.CHECKBOX;
		userSetting.value = true;

		presenter.onSettingsClicked(userSetting);

		verify(userService).setUserSetting(userSettingArgumentCaptor.capture());
		UserSetting value = userSettingArgumentCaptor.getValue();
		assertFalse("Boolean value expected to be changed", (Boolean) value.value);
	}
}