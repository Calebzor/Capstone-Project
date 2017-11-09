package hu.tvarga.capstone.cheaplist.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.capstone.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;
import hu.tvarga.capstone.cheaplist.utility.eventbus.Event;
import timber.log.Timber;

@ApplicationScope
public class UserService implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private final Event event;
	private DatabaseReference databaseReferenceUser;

	private List<UserCategoryFilterListItem> categoriesFilterForUser = new ArrayList<>();
	private DatabaseReference categoriesFilterForUserDB;

	@Inject
	UserService(Event event) {
		this.event = event;
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
		categoriesFilterForUserDB.orderByChild("category").addValueEventListener(
				new ValueEventListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						Iterable<DataSnapshot> children = dataSnapshot.getChildren();
						List<UserCategoryFilterListItem> categoriesFilterForUserOld =
								new ArrayList<>();
						categoriesFilterForUserOld.addAll(categoriesFilterForUser);
						categoriesFilterForUser.clear();
						for (DataSnapshot child : children) {
							UserCategoryFilterListItem item = child.getValue(
									UserCategoryFilterListItem.class);
							categoriesFilterForUser.add(item);
						}

						CompareSettingsFilterChangedBroadcastObject broadcastObject =
								new CompareSettingsFilterChangedBroadcastObject(
										categoriesFilterForUserOld, categoriesFilterForUser);
						event.post(broadcastObject);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("categoriesFilterForUserDB#onCancelled", databaseError);
					}
				});
	}

	public void setCategoriesFilterForUser(
			List<UserCategoryFilterListItem> categoriesFilterForUser) {
		if (databaseReferenceUser == null) {
			return;
		}
		categoriesFilterForUserDB.setValue(categoriesFilterForUser);
	}

	public List<UserCategoryFilterListItem> getCategoriesFilterForUser() {
		return categoriesFilterForUser;
	}
}
