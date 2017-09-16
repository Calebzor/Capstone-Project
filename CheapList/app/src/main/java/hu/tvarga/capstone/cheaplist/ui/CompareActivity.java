package hu.tvarga.capstone.cheaplist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.BuildConfig;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import timber.log.Timber;

public class CompareActivity extends AppCompatActivity {

	public static final String ANONYMOUS = "anonymous";
	public static final int RC_SIGN_IN = 1;
	public static final String PAGE_ITEM = "pageItem";

	@BindView(R.id.pager)
	ViewPager pager;

	@BindView(R.id.tabs)
	TabLayout tabLayout;

	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;
	private String userName;
	private DatabaseReference databaseReferencePublic;
	private CategoryPagerAdapter pagerAdapter;
	private int currentPage;

	private List<String> categories = new ArrayList<>();
	private HashMap<String, Merchant> merchantMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		ButterKnife.bind(this);

		if (savedInstanceState != null) {
			currentPage = savedInstanceState.getInt(PAGE_ITEM, 0);
		}

		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		firebaseAuth = FirebaseAuth.getInstance();
		FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		databaseReferencePublic = firebaseDatabase.getReference().child("publicReadable");

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

		pagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		firebaseAuth.addAuthStateListener(authStateListener);
		getCategories();
		getMerchants();
	}

	private void getMerchants() {
		databaseReferencePublic.child("merchants").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<HashMap<String, Merchant>> genericTypeIndicator =
								new GenericTypeIndicator<HashMap<String, Merchant>>() {};
						merchantMap = dataSnapshot.getValue(genericTypeIndicator);
						gotMerchants();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("getMerchants#onCancelled", databaseError);
					}
				});
	}

	private void gotMerchants() {
		Timber.d("gotMerchants");
		getMerchantCategoryData();
	}

	private void gotCategories() {
		Timber.d("gotCategories");
		getMerchantCategoryData();
	}

	private void getMerchantCategoryData() {
		if (isMerchantAndCategoryAvailable()) {
			pagerAdapter.notifyDataSetChanged();
			pager.setCurrentItem(currentPage);
		}
	}

	private boolean isMerchantAndCategoryAvailable() {
		return merchantMap != null && !merchantMap.isEmpty() && !categories.isEmpty();
	}

	private void getCategories() {
		databaseReferencePublic.child("itemCategories").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						GenericTypeIndicator<Map<String, String>> genericTypeIndicator =
								new GenericTypeIndicator<Map<String, String>>() {};
						Map<String, String> categoriesFromDB = dataSnapshot.getValue(
								genericTypeIndicator);
						if (categoriesFromDB != null) {
							categories.clear();
							for (Map.Entry<String, String> pair : categoriesFromDB.entrySet()) {
								categories.add(pair.getValue());
							}
							Collections.sort(categories);
							gotCategories();
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Timber.d("addAuthStateListener#onCancelled", databaseError);
					}
				});
	}


	@Override
	protected void onPause() {
		super.onPause();
		if (authStateListener != null) {
			firebaseAuth.removeAuthStateListener(authStateListener);
		}
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(PAGE_ITEM, pager.getCurrentItem());
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
	}

	private void onSignedOutCleanUp() {
		userName = ANONYMOUS;
	}

	private class CategoryPagerAdapter extends FragmentStatePagerAdapter {

		CategoryPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			return CompareFragment.newInstance(getCategoryName(position), merchantMap);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getCategoryName(position);
		}

		private String getCategoryName(int position) {
			String title = getString(R.string.category);
			if (position < categories.size()) {
				title = categories.get(position);
			}
			return title;
		}

		@Override
		public int getCount() {
			return categories.size();
		}
	}
}
