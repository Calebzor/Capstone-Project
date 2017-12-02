package hu.tvarga.cheaplist.ui.compare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.CompareContract;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.MainActivity;
import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsDialog;
import hu.tvarga.cheaplist.utility.StringUtils;

public class CompareFragment extends DaggerFragment
		implements CompareContract.View, MainActivity.SearchHandler {

	public static final String FRAGMENT_TAG = CompareFragment.class.getName();

	@BindView(R.id.startDefaultText)
	TextView startDefaultText;
	@BindView(R.id.itemsListStart)
	RecyclerView startItems;

	@BindView(R.id.endDefaultText)
	TextView endDefaultText;
	@BindView(R.id.itemListEnd)
	RecyclerView endItems;

	@Inject
	CompareContract.Presenter comparePresenter;

	private Unbinder unbinder;

	private SearchView searchView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.compareFilterMenuItem) {
			CompareSettingsDialog compareSettingsDialog = new CompareSettingsDialog();
			compareSettingsDialog.show(getActivity().getSupportFragmentManager(),
					CompareSettingsDialog.FRAGMENT_TAG);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		comparePresenter.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		searchView.setOnQueryTextListener(null);
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
	public void setOnQueryTextListener(SearchView searchView, MenuItem item) {
		this.searchView = searchView;
		String filter = comparePresenter.getFilter();
		if (!StringUtils.isEmpty(filter)) {
			item.expandActionView();
			searchView.setQuery(filter, false);
		}
		searchView.setOnQueryTextListener(comparePresenter.getOnQueryTextListener());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		comparePresenter.setStartAdapter(startItems);
		comparePresenter.setEndAdapter(endItems);
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
}
