package hu.tvarga.cheaplist.ui.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.CompareContract;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.MainActivity;
import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsDialog;

public class CompareFragment extends DaggerFragment implements CompareContract.View {

	public static final String FRAGMENT_TAG = CompareFragment.class.getName();

	@BindView(R.id.startDefaultText)
	TextView startDefaultText;
	@BindView(R.id.itemsListStart)
	RecyclerView startItems;

	@BindView(R.id.endDefaultText)
	TextView endDefaultText;
	@BindView(R.id.itemListEnd)
	RecyclerView endItems;

	@BindView(R.id.compareFilterButton)
	AppCompatButton compareFilterButton;

	@BindView(R.id.compareSearchInput)
	SearchView compareSearchInput;

	@BindView(R.id.compareProgressBar)
	ProgressBar progressBar;

	@Inject
	CompareContract.Presenter comparePresenter;

	private Unbinder unbinder;

	@Override
	public void onResume() {
		super.onResume();
		comparePresenter.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		comparePresenter.onPause();
	}

	@Override
	public View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item,
			final MerchantCategoryListItemHolder holder) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((MainActivity) getActivity()).openDetailView(item, holder);
			}
		};
	}

	@Override
	public View getActivityCoordinatorLayout() {
		View view = null;
		FragmentActivity activity = getActivity();
		if (activity != null) {
			view = activity.findViewById(R.id.coordinator);
		}
		return view;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_compare, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		comparePresenter.getAndSetStartAdapter(startItems);
		comparePresenter.getAndSetEndAdapter(endItems);
		compareSearchInput.setOnQueryTextListener(comparePresenter.getOnQueryTextListener());
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void setStartEmptyView(int itemCount) {
		if (itemCount == 0) {
			startDefaultText.setText(R.string.no_data_available);
		}
		startDefaultText.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setEndEmptyView(int itemCount) {
		if (itemCount == 0) {
			endDefaultText.setText(R.string.no_data_available);
		}
		endDefaultText.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void notifyGotMerchantCategoryData(final List<ItemCategory> categories) {
		progressBar.setVisibility(View.INVISIBLE);
		compareFilterButton.setVisibility(View.VISIBLE);

		compareFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CompareSettingsDialog compareSettingsDialog = new CompareSettingsDialog();
				compareSettingsDialog.show(getActivity().getFragmentManager(),
						CompareSettingsDialog.FRAGMENT_TAG);
			}
		});
	}
}