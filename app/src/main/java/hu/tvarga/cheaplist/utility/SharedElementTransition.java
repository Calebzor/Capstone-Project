package hu.tvarga.cheaplist.utility;

import android.support.v4.view.ViewCompat;
import android.view.View;

import hu.tvarga.cheaplist.dao.Merchant;

public class SharedElementTransition {

	private SharedElementTransition() {
		// hiding constructor for utility class
	}

	public static void setTransitionName(View view, Merchant merchant, String key) {
		ViewCompat.setTransitionName(view, key + merchant.name + "_itemImage");
	}
}
