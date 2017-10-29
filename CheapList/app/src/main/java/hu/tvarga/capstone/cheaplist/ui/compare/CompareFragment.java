package hu.tvarga.capstone.cheaplist.ui.compare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.compare.CompareContract;
import hu.tvarga.capstone.cheaplist.business.compare.ComparePresenter;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListActivity;

import static hu.tvarga.capstone.cheaplist.R.bool.multipane;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.IMAGE_TRANSITION_NAME;

public class CompareFragment extends DaggerFragment implements CompareContract.View {

	public static final String ARG_CATEGORY = "ARG_CATEGORY";
	private static final String ARG_MERCHANT_MAP = "ARG_MERCHANT_MAP";

	@BindView(R.id.startEmptyText)
	TextView startEmptyText;
	@BindView(R.id.itemsListStart)
	RecyclerView startItems;

	@BindView(R.id.endEmptyText)
	TextView endEmptyText;
	@BindView(R.id.itemListEnd)
	RecyclerView endItems;

	@BindView(R.id.compareFilterButton)
	AppCompatImageView compareFilterButton;

	@BindView(R.id.compareProgressBar)
	ProgressBar progressBar;

	@Inject
	ComparePresenter presenter;

	private Unbinder unbinder;

	public static Fragment newInstance(String categoryName, Map<String, Merchant> merchantMap) {
		Bundle arguments = new Bundle();
		arguments.putString(ARG_CATEGORY, categoryName);
		arguments.putSerializable(ARG_MERCHANT_MAP, (HashMap<String, Merchant>) merchantMap);
		CompareFragment fragment = new CompareFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onPause() {
		super.onPause();
		presenter.onPause();
	}

	@Override
	public View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item,
			final MerchantCategoryListItemHolder holder) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Class<?> targetActivity = DetailActivity.class;
				if (getResources().getBoolean(multipane)) {
					targetActivity = ShoppingListActivity.class;
				}
				Intent intent = new Intent(getContext(), targetActivity);
				String transitionName = holder.image.getTransitionName();
				ActivityOptionsCompat options = ActivityOptionsCompat.
						makeSceneTransitionAnimation(getActivity(), holder.image, transitionName);
				intent.putExtra(DETAIL_ITEM, item);
				intent.putExtra(IMAGE_TRANSITION_NAME, transitionName);
				startActivity(intent, options.toBundle());
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
	public void onResume() {
		super.onResume();
		presenter.onResume(this);
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
		RecyclerView.Adapter<MerchantCategoryListItemHolder> startAdapter =
				presenter.getStartAdapter();
		startItems.setAdapter(startAdapter);

		RecyclerView.Adapter<MerchantCategoryListItemHolder> endAdapter = presenter.getEndAdapter();
		endItems.setAdapter(endAdapter);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void setStartEmptyView(int itemCount) {
		startEmptyText.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setEndEmptyView(int itemCount) {
		endEmptyText.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void notifyGotMerchantCategoryData(final List<String> categories) {
		progressBar.setVisibility(View.INVISIBLE);
		compareFilterButton.setVisibility(View.VISIBLE);

		compareFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				StringBuilder sb = new StringBuilder();
				for (String category : categories) {
					sb.append(category).append("\n");
				}
				String text = sb.toString();
				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
