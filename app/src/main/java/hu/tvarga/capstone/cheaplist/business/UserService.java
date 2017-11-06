package hu.tvarga.capstone.cheaplist.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.utility.EventBusWrapper;
import timber.log.Timber;

@ApplicationScope
public class UserService implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private final EventBusWrapper eventBusWrapper;
	private DatabaseReference databaseReferenceUser;

	private Map<String, Boolean> categoriesFilterForUser = new HashMap<>();
	private DatabaseReference categoriesFilterForUserDB;

	@Inject
	UserService(EventBusWrapper eventBusWrapper) {
		this.eventBusWrapper = eventBusWrapper;
		firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.addAuthStateListener(this);
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if (currentUser != null) {
			databaseReferenceUser = firebaseDatabase.getReference().child("userData").child(
					currentUser.getUid());
			categoriesFilterForUserDB = databaseReferenceUser.child("categoriesFilterForUser");
			addCategoriesFilterForUserDBListener();
		}
		else {
			databaseReferenceUser = null;
		}
	}

	private void addCategoriesFilterForUserDBListener() {
		categoriesFilterForUserDB.addValueEventListener(new ValueEventListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Map<String, Boolean> categoriesFilterForUserFromDB =
						(Map<String, Boolean>) dataSnapshot.getValue();
				categoriesFilterForUser.clear();
				if (categoriesFilterForUserFromDB != null) {
					categoriesFilterForUser.putAll(categoriesFilterForUserFromDB);
				}
				eventBusWrapper.getDefault().post(
						new CompareSettingsFilterChangedBroadcastObject());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Timber.d("categoriesFilterForUserDB#onCancelled", databaseError);
			}
		});
	}

	public void setCategoriesFilterForUser(Map<String, Boolean> categoriesFilterForUser) {
		if (databaseReferenceUser == null) {
			return;
		}
		categoriesFilterForUserDB.setValue(categoriesFilterForUser);
	}

	public Map<String, Boolean> getCategoriesFilterForUser() {
		return categoriesFilterForUser;
	}
}
