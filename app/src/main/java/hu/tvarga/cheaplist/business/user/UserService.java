package hu.tvarga.cheaplist.business.user;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import hu.tvarga.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItems;
import hu.tvarga.cheaplist.dao.UserSetting;
import hu.tvarga.cheaplist.dao.UserSettingType;
import hu.tvarga.cheaplist.dao.UserSettings;
import hu.tvarga.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.cheaplist.utility.GsonHelper;
import hu.tvarga.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

@ApplicationScope
public class UserService implements FirebaseAuth.AuthStateListener {

	private static final String CATEGORIES_FILTER_FOR_USER = "categoriesFilterForUser";
	private static final String USER_SETTINGS = "userSettings";
	private final FirebaseFirestore db;
	private final Event event;
	private DocumentReference databaseReferenceUser;

	private List<UserCategoryFilterListItem> categoriesFilterForUser = new ArrayList<>();
	private List<UserSetting> userSettings = new ArrayList<>();
	private static Map<UserSettingStringsMap, UserSetting> userSettingMap = new EnumMap<>(
			UserSettingStringsMap.class);
	private CollectionReference categoriesFilterForUserDB;
	private CollectionReference userSettingsDB;

	@Inject
	UserService(Event event, FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
		this.event = event;
		this.db = firebaseFirestore;
		firebaseAuth.addAuthStateListener(this);
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if (currentUser != null) {
			databaseReferenceUser = db.collection("userData").document(currentUser.getUid());
			categoriesFilterForUserDB = databaseReferenceUser.collection(
					CATEGORIES_FILTER_FOR_USER);
			userSettingsDB = databaseReferenceUser.collection(USER_SETTINGS);
			addCategoriesFilterForUserDBListener();
			addUserSettingsDBListener();
		}
		else {
			databaseReferenceUser = null;
		}
	}

	private void addUserSettingsDBListener() {
		addDefaultValuesIfMissing();
		if (userSettingsDB == null) {
			Timber.d("userSettingsDB was null, user not logged in?");
			return;
		}
		userSettingsDB.document(USER_SETTINGS).addSnapshotListener(
				new EventListener<DocumentSnapshot>() {
					@Override
					public void onEvent(DocumentSnapshot documentSnapshot,
							FirebaseFirestoreException e) {
						if (e != null) {
							Timber.e("userSettingsDB#onEvent", e);
							return;
						}
						if (documentSnapshot != null && documentSnapshot.exists()) {
							userSettings.clear();
							@SuppressWarnings("unchecked")
							List<Map<String, Object>> userSettingsFromDB =
									(List<Map<String, Object>>) documentSnapshot.get(
											UserSettings.FIELD_NAME);
							Gson gson = GsonHelper.getGson();
							for (Map<String, Object> userSettingListItem : userSettingsFromDB) {
								JsonElement jsonElement = gson.toJsonTree(userSettingListItem);
								UserSetting item = gson.fromJson(jsonElement, UserSetting.class);
								userSettings.add(item);
								userSettingMap.put(UserSettingStringsMap.valueOf(item.name), item);
							}
						}
					}
				});
	}

	private void addDefaultValuesIfMissing() {
		boolean hasImageDownloadSetting = false;
		for (UserSetting userSetting : userSettings) {
			if (!hasImageDownloadSetting && userSetting.name.equals(
					UserSettingStringsMap.IMAGE_DOWNLOADING_DISABLED.name())) {
				hasImageDownloadSetting = true;
			}
		}
		if (!hasImageDownloadSetting) {
			UserSetting imageDownloadSetting = new UserSetting();
			imageDownloadSetting.name = UserSettingStringsMap.IMAGE_DOWNLOADING_DISABLED.name();
			imageDownloadSetting.userSettingType = UserSettingType.CHECKBOX;
			imageDownloadSetting.value = false;
			userSettings.add(imageDownloadSetting);
			userSettingMap.put(UserSettingStringsMap.IMAGE_DOWNLOADING_DISABLED,
					imageDownloadSetting);
		}

	}

	private static UserSetting getUserSetting(UserSettingStringsMap userSettingStringsMap) {
		return userSettingMap.get(userSettingStringsMap);
	}

	public static boolean shouldDownloadImages() {
		UserSetting userSetting = getUserSetting(UserSettingStringsMap.IMAGE_DOWNLOADING_DISABLED);
		return userSetting == null || !(Boolean) userSetting.value;
	}

	public void initiateGetCategoriesFilterForUser() {
		addCategoriesFilterForUserDBListener();
	}

	private void addCategoriesFilterForUserDBListener() {
		if (categoriesFilterForUserDB == null) {
			Timber.d("categoriesFilterForUserDB was null, user not logged in?");
			return;
		}
		categoriesFilterForUserDB.document(CATEGORIES_FILTER_FOR_USER).addSnapshotListener(
				new EventListener<DocumentSnapshot>() {
					@Override
					public void onEvent(DocumentSnapshot documentSnapshot,
							FirebaseFirestoreException e) {
						if (e != null) {
							Timber.e("categoriesFilterForUserDB#onEvent", e);
							return;
						}
						List<UserCategoryFilterListItem> categoriesFilterForUserOld =
								new ArrayList<>();
						categoriesFilterForUserOld.addAll(categoriesFilterForUser);
						if (documentSnapshot != null && documentSnapshot.exists()) {
							categoriesFilterForUser.clear();
							@SuppressWarnings("unchecked")
							List<Map<String, Object>> userCategoryFilterListItems =
									(List<Map<String, Object>>) documentSnapshot.get(
											UserCategoryFilterListItems.FIELD_NAME);
							Gson gson = GsonHelper.getGson();
							for (Map<String, Object> userCategoryFilterListItem : userCategoryFilterListItems) {
								JsonElement jsonElement = gson.toJsonTree(
										userCategoryFilterListItem);
								UserCategoryFilterListItem item = gson.fromJson(jsonElement,
										UserCategoryFilterListItem.class);
								categoriesFilterForUser.add(item);
							}
						}
						CompareSettingsFilterChangedBroadcastObject broadcastObject =
								new CompareSettingsFilterChangedBroadcastObject(
										categoriesFilterForUserOld, categoriesFilterForUser);
						event.post(broadcastObject);
					}
				});
	}

	public void setCategoriesFilterForUser(
			List<UserCategoryFilterListItem> categoriesFilterForUser) {
		if (databaseReferenceUser == null) {
			return;
		}
		categoriesFilterForUserDB.document(CATEGORIES_FILTER_FOR_USER).set(
				new UserCategoryFilterListItems(categoriesFilterForUser));
	}

	public List<UserCategoryFilterListItem> getCategoriesFilterForUser() {
		return categoriesFilterForUser;
	}

	public List<UserSetting> getUserSettings() {
		return userSettings;
	}

	public void setUserSetting(UserSetting userSetting) {
		if (databaseReferenceUser == null) {
			return;
		}
		for (int i = 0; i < userSettings.size(); i++) {
			if (userSettings.get(i).name.equals(userSetting.name)) {
				userSettings.set(i, userSetting);
				break;
			}
		}

		userSettingsDB.document(USER_SETTINGS).set(new UserSettings(userSettings));
	}
}
