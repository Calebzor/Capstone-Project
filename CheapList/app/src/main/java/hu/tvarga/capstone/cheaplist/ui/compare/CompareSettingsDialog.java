package hu.tvarga.capstone.cheaplist.ui.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.DaggerDialogFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.compare.Settings.CompareSettingsContract;

public class CompareSettingsDialog extends DaggerDialogFragment
		implements CompareSettingsContract.View {

	public static final String FRAGMENT_TAG = CompareSettingsDialog.class.getName();

	@Inject
	CompareSettingsContract.Presenter presenter;

	private Unbinder unbinder;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_compare_settings, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		presenter.onStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		presenter.onStop();
	}

	@Override
	public void onDestroyView() {
		unbinder.unbind();
		super.onDestroyView();
	}
}
