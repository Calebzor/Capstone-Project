package hu.tvarga.capstone.cheaplist.ui.compare;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.BuildConfig;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.ui.AuthBaseActivity;
import timber.log.Timber;

public class CompareActivity extends AuthBaseActivity {

	public static final String PAGE_ITEM = "pageItem";

	@BindView(R.id.pager)
	ViewPager pager;

	@BindView(R.id.tabs)
	TabLayout tabLayout;

	private DatabaseReference databaseReferencePublic;
	private CategoryPagerAdapter pagerAdapter;
	private int currentPage;

	private List<String> categories = new ArrayList<>();
	private HashMap<String, Merchant> merchantMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		ButterKnife.bind(this);

		if (savedInstanceState != null) {
			currentPage = savedInstanceState.getInt(PAGE_ITEM, 0);
		}

		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		databaseReferencePublic = firebaseDatabase.getReference().child("publicReadable");

		FirebaseRemoteConfigSettings configSettings =
				new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(
						BuildConfig.DEBUG).build();
		firebaseRemoteConfig.setConfigSettings(configSettings);

		pagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
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
						Timber.d("getMerchants#onCancelled %s", databaseError);
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
						Timber.d("addAuthStateListener#onCancelled %s", databaseError);
					}
				});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		currentPage = pager.getCurrentItem();
		outState.putInt(PAGE_ITEM, currentPage);
		super.onSaveInstanceState(outState);
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
