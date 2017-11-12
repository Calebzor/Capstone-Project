package hu.tvarga.cheaplist.ui;

import android.os.Bundle;
import android.view.Menu;

import hu.tvarga.cheaplist.R;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class StartActivity extends AuthBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	protected void setUpSearchView(Menu menu) {
		// nothing to do here
	}
}
