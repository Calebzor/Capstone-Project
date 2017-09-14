package hu.tvarga.capstone.cheaplist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Arrays;

public class CompareActivity extends AppCompatActivity {

	public static final String ANONYMOUS = "anonymous";
	public static final int RC_SIGN_IN = 1;

	private FirebaseDatabase firebaseDatabase;
	private FirebaseAuth firebaseAuth;
	private FirebaseRemoteConfig firebaseRemoteConfig;
	private FirebaseAuth.AuthStateListener authStateListener;
	private String userName;
	private ChildEventListener childEventListener;
	private DatabaseReference databaseReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);

		firebaseDatabase = FirebaseDatabase.getInstance();
		firebaseAuth = FirebaseAuth.getInstance();
		firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		databaseReference = firebaseDatabase.getReference().child("publicReadable");

		userName = ANONYMOUS;

		FirebaseRemoteConfigSettings configSettings =
				new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(
						BuildConfig.DEBUG).build();
		firebaseRemoteConfig.setConfigSettings(configSettings);

		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					onSignedInInitialize(user.getDisplayName());
				}
				else {
					onSignedOutCleanUp();
					startActivityForResult(
							AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(
									false).setAvailableProviders(Arrays.asList(
									new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
									new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
									.build(), RC_SIGN_IN);
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		firebaseAuth.addAuthStateListener(authStateListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (authStateListener != null) {
			firebaseAuth.removeAuthStateListener(authStateListener);
		}
		detachDatabaseReadListener();
		//		mMessageAdapter.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sign_out_menu:
				AuthUI.getInstance().signOut(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RC_SIGN_IN == requestCode) {
			if (RESULT_OK == resultCode) {
				Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
			}
			else if (RESULT_CANCELED == resultCode) {
				Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void onSignedInInitialize(String displayName) {
		userName = displayName;
		attachDatabaseReadListener();
	}

	private void onSignedOutCleanUp() {
		userName = ANONYMOUS;
		//		mMessageAdapter.clear();
		detachDatabaseReadListener();
	}

	private void attachDatabaseReadListener() {
		if (childEventListener == null) {
			childEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {
					//					FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
					//					mMessageAdapter.add(friendlyMessage);
				}

				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String s) {

				}

				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) {

				}

				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			};
		}
		databaseReference.addChildEventListener(childEventListener);
	}

	private void detachDatabaseReadListener() {
		if (childEventListener != null) {
			databaseReference.removeEventListener(childEventListener);
			childEventListener = null;
		}
	}
}
