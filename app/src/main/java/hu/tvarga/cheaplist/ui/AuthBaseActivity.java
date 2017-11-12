package hu.tvarga.cheaplist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.ui.compare.CompareFragment;
import hu.tvarga.cheaplist.ui.shoppinglist.ShoppingListFragment;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public abstract class AuthBaseActivity extends DaggerAppCompatActivity {

	public static final int RC_SIGN_IN = 1;

	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		firebaseAuth = FirebaseAuth.getInstance();

		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user == null) {
					startActivityForResult(
							AuthUI.getInstance().createSignInIntentBuilder().setTheme(getAppTheme())
									.setAvailableProviders(Arrays.asList(
											new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
													.build(), new AuthUI.IdpConfig.Builder(
													AuthUI.FACEBOOK_PROVIDER)
													.setPermissions(getFacebookPermissions())
													.build(),
											new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER)
													.build())).setAllowNewEmailAccounts(true)
									.build(), RC_SIGN_IN);
				}
			}
		};
	}

	@MainThread
	private List<String> getFacebookPermissions() {
		List<String> result = new ArrayList<>();
		result.add("user_friends");
		result.add("user_photos");
		return result;
	}

	private int getAppTheme() {
		return R.style.AppTheme;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	private DaggerFragment getFragmentByTag(String fragmentTag) {
		return (DaggerFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		DaggerFragment fragmentByTag;
		String fragmentTag;
		switch (item.getItemId()) {
			case R.id.sign_out_menu:
				AuthUI.getInstance().signOut(this);
				return true;
			case R.id.shoppingListMenuItem:
				fragmentTag = ShoppingListFragment.FRAGMENT_TAG;
				fragmentByTag = getFragmentByTag(fragmentTag);
				if (fragmentByTag == null) {
					fragmentByTag = ShoppingListFragment.newInstance();
				}
				replaceFragment(fragmentByTag);
				return true;
			case R.id.compareMenuItem:
				fragmentTag = CompareFragment.FRAGMENT_TAG;
				fragmentByTag = getFragmentByTag(fragmentTag);
				if (fragmentByTag == null) {
					fragmentByTag = new CompareFragment();
				}
				replaceFragment(fragmentByTag);
				return true;
			default:
		}
		return super.onOptionsItemSelected(item);
	}

	protected void replaceFragment(DaggerFragment fragment) {
		String backStateName = fragment.getClass().getName();

		FragmentManager manager = getSupportFragmentManager();
		boolean fragmentPopped;
		if (fragment instanceof CompareFragment || fragment instanceof ShoppingListFragment) {
			fragmentPopped = manager.popBackStackImmediate(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			if (fragment instanceof ShoppingListFragment && fragmentPopped) {
				manager.beginTransaction().replace(R.id.mainActivityFragmentContainer, fragment)
						.addToBackStack(backStateName).commit();
			}
		}
		else {
			fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
		}

		if (!fragmentPopped) { //fragment not in back stack, create it.
			manager.beginTransaction().replace(R.id.mainActivityFragmentContainer, fragment)
					.addToBackStack(backStateName).commit();
		}
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			handleSignInResponse(resultCode, data);
			return;
		}

		showToast(R.string.unknown_response);
	}

	@MainThread
	private void handleSignInResponse(int resultCode, Intent data) {
		IdpResponse response = IdpResponse.fromResultIntent(data);

		// Successfully signed in
		if (resultCode == RESULT_OK) {
			showToast(R.string.signed_in);
			return;
		}
		else {
			// Sign in failed
			if (response == null) {
				// User pressed back button
				showToast(R.string.sign_in_cancelled);
				return;
			}

			if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
				showToast(R.string.no_internet_connection);
				return;
			}

			if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
				showToast(R.string.unknown_error);
				return;
			}
		}

		showToast(R.string.unknown_sign_in_response);
	}

	@MainThread
	private void showToast(@StringRes int errorMessageRes) {
		Toast.makeText(this, errorMessageRes, Toast.LENGTH_SHORT).show();
	}
}
