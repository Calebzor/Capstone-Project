package hu.tvarga.cheaplist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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
		if (RC_SIGN_IN == requestCode) {
			if (RESULT_OK == resultCode) {
				Toast.makeText(this, getString(R.string.signed_in), Toast.LENGTH_SHORT).show();
			}
			else if (RESULT_CANCELED == resultCode) {
				Toast.makeText(this, getString(R.string.sign_in_canceled), Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		}
	}
}
