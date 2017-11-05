package hu.tvarga.capstone.cheaplist.utility;

import android.content.Context;
import android.content.SharedPreferences;

import hu.tvarga.capstone.cheaplist.BuildConfig;

public class Preferences {

	private Preferences() {
		// hide constructor
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
	}
}
