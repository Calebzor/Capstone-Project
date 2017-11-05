package hu.tvarga.capstone.cheaplist.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.utility.Preferences;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public abstract class AdShowingActivity extends AuthBaseActivity {

	public static final String DETAIL_OPEN_COUNT = "DETAIL_OPEN_COUNT";
	public static final int AD_SHOW_DETAIL_OPEN_COUNT_TRIGGER = 10;

	private InterstitialAd interstitialAd;
	private SharedPreferences sharedPreferences;
	private int detailOpenCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPreferences = Preferences.getSharedPreferences(this);
		detailOpenCount = sharedPreferences.getInt(DETAIL_OPEN_COUNT, 0);
		detailOpenCount++;
		sharedPreferences.edit().putInt(DETAIL_OPEN_COUNT, detailOpenCount).apply();

		MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitialAd.loadAd(adRequest);

		interstitialAd.setAdListener(adListener);
	}

	private AdListener adListener = new AdListener() {
		@Override
		public void onAdClosed() {
			loadNextAd();
		}

		@Override
		public void onAdLoaded() {
			if (shouldShowAd()) {
				showInterstitial();
			}
		}

		private boolean shouldShowAd() {
			if (detailOpenCount >= AD_SHOW_DETAIL_OPEN_COUNT_TRIGGER) {
				detailOpenCount = 0;
				sharedPreferences.edit().putInt(DETAIL_OPEN_COUNT, detailOpenCount).apply();
				return true;
			}
			return false;
		}

		private void showInterstitial() {
			if (interstitialAd != null && interstitialAd.isLoaded()) {
				interstitialAd.show();
			}
		}

		private void loadNextAd() {
			if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
				AdRequest adRequest = new AdRequest.Builder().build();
				interstitialAd.loadAd(adRequest);
			}
		}
	};

}
