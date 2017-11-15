package hu.tvarga.cheaplist.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import hu.tvarga.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

@ApplicationScope
public class UserService implements FirebaseAuth.AuthStateListener {

	private static final String CATEGORIES_FILTER_FOR_USER = "categoriesFilterForUser";
	private final FirebaseFirestore db;
	private final Event event;
	private DocumentReference databaseReferenceUser;

	private List<UserCategoryFilterListItem> categoriesFilterForUser = new ArrayList<>();
	private CollectionReference categoriesFilterForUserDB;

	@Inject
	UserService(Event event) {
		this.event = event;
		db = FirebaseFirestore.getInstance();
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.addAuthStateListener(this);
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if (currentUser != null) {
			databaseReferenceUser = db.collection("userData").document(currentUser.getUid());
			categoriesFilterForUserDB = databaseReferenceUser.collection(
					CATEGORIES_FILTER_FOR_USER);
			addCategoriesFilterForUserDBListener();
		}
		else {
			databaseReferenceUser = null;
		}
	}

	class UserCategoryFilterListItemForDB {

		List<UserCategoryFilterListItem> userCategoryFilterListItems;

		UserCategoryFilterListItemForDB(
				List<UserCategoryFilterListItem> userCategoryFilterListItems) {
			this.userCategoryFilterListItems = userCategoryFilterListItems;
		}
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
							Timber.d("categoriesFilterForUserDB#onCancelled", e);
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
											"userCategoryFilterListItems");
							for (Map<String, Object> userCategoryFilterListItem : userCategoryFilterListItems) {
								UserCategoryFilterListItem item = new UserCategoryFilterListItem();
								item.category = ItemCategory.valueOf(
										(String) userCategoryFilterListItem.get("category"));
								item.checked = (boolean) userCategoryFilterListItem.get("checked");
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
				new UserCategoryFilterListItemForDB(categoriesFilterForUser));
	}

	public List<UserCategoryFilterListItem> getCategoriesFilterForUser() {
		return categoriesFilterForUser;
	}
}
