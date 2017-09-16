package hu.tvarga.capstone.cheaplist.business;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.di.scopes.ApplicationScope;

@ApplicationScope
public class ShoppingListManager implements FirebaseAuth.AuthStateListener {

	private final FirebaseDatabase firebaseDatabase;
	private DatabaseReference databaseReferenceUser;

	@Inject
	public ShoppingListManager() {
		firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.addAuthStateListener(this);
	}

	@Override
	public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
		FirebaseUser user = firebaseAuth.getCurrentUser();
		if (user != null) {
			databaseReferenceUser = firebaseDatabase.getReference().child("users").child(
					user.getUid());
		}
		else {
			databaseReferenceUser = null;
		}
	}

	public void addToList(String itemID) {
		if (databaseReferenceUser == null) {
			return;
		}
	}

	public void removeFromList(String itemID) {
		if (databaseReferenceUser == null) {
			return;
		}
	}
}
