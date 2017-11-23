package hu.tvarga.cheaplist.dao;

import com.google.cloud.firestore.annotation.Exclude;

import java.util.List;

public class UserSettings {

	@Exclude
	public static final String FIELD_NAME = "userSettings";

	public List<UserSetting> userSettings;

	public UserSettings(List<UserSetting> userSettings) {
		this.userSettings = userSettings;
	}
}
