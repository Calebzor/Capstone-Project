package hu.tvarga.capstone.cheaplist.ui.compare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity;
import hu.tvarga.capstone.cheaplist.ui.shoppinglist.ShoppingListActivity;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.R.bool.multipane;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.IMAGE_TRANSITION_NAME;

public class CompareFragment extends DaggerFragment {

	public static final String ARG_CATEGORY = "ARG_CATEGORY";
	private static final String ARG_MERCHANT_MAP = "ARG_MERCHANT_MAP";
	public static final String START_POS_INDEX = "START_POS_INDEX";
	public static final String START_TOP_VIEW = "START_TOP_VIEW";
	public static final String END_POS_INDEX = "END_POS_INDEX";
	public static final String END_TOP_VIEW = "END_TOP_VIEW";

	@BindView(R.id.startEmptyText)
	TextView startEmptyText;
	@BindView(R.id.itemsListStart)
	RecyclerView startItems;

	@BindView(R.id.endEmptyText)
	TextView endEmptyText;
	@BindView(R.id.itemListEnd)
	RecyclerView endItems;

	@Inject
	ShoppingListManager shoppingListManager;

	private String category;
	private Unbinder unbinder;
	private HashMap<String, Merchant> merchantMap;
	protected DatabaseReference startMerchantItemsDBRef;
	protected DatabaseReference endMerchantItemsDBRef;
	private Merchant startMerchant;
	private Merchant endMerchant;
	private LinearLayoutManager startLayoutManager;
	private LinearLayoutManager endLayoutManager;
	private int startPositionIndex;
	private int endPositionIndex;
	private int startTopView;
	private int endTopView;

	public static Fragment newInstance(String categoryName, Map<String, Merchant> merchantMap) {
		Bundle arguments = new Bundle();
		arguments.putString(ARG_CATEGORY, categoryName);
		arguments.putSerializable(ARG_MERCHANT_MAP, (HashMap<String, Merchant>) merchantMap);
		CompareFragment fragment = new CompareFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_CATEGORY)) {
			category = getArguments().getString(ARG_CATEGORY);
		}
		if (getArguments().containsKey(ARG_MERCHANT_MAP)) {
			merchantMap = (HashMap<String, Merchant>) getArguments().getSerializable(
					ARG_MERCHANT_MAP);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		attachRecyclerViewAdapter();
	}

	private DatabaseReference getDBRefForMerchantCategoryList(Map.Entry<String, Merchant> entry) {
		String key = entry.getKey() + category;
		return FirebaseDatabase.getInstance().getReference().child("publicReadable").child(
				"merchantCategoryListItems").child(key);
	}

	private void attachRecyclerViewAdapter() {
		Timber.d("attachRecyclerViewAdapter");
		FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder>
				startAdapter = getStartAdapter();
		FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder>
				endAdapter = getEndAdapter();

		startItems.setAdapter(startAdapter);
		endItems.setAdapter(endAdapter);
	}

	protected FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder> getStartAdapter() {
		Query query = startMerchantItemsDBRef.orderByChild("name");
		return new FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder>(
				MerchantCategoryListItem.class, R.layout.merchant_category_list_item_start,
				MerchantCategoryListItemHolder.class, query, this) {
			@Override
			public void populateViewHolder(MerchantCategoryListItemHolder holder,
					MerchantCategoryListItem item, int position) {
				holder.bind(item, getActivityCoordinatorLayout(), shoppingListManager,
						startMerchant,
						getOnListItemOnClickListener(new ShoppingListItem(item, startMerchant),
								holder), position);
			}

			@Override
			public void onDataChanged() {
				startEmptyText.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
			}
		};
	}

	protected FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder> getEndAdapter() {
		Query query = endMerchantItemsDBRef.orderByChild("name");
		return new FirebaseRecyclerAdapter<MerchantCategoryListItem, MerchantCategoryListItemHolder>(
				MerchantCategoryListItem.class, R.layout.merchant_category_list_item_end,
				MerchantCategoryListItemHolder.class, query, this) {
			@Override
			public void populateViewHolder(MerchantCategoryListItemHolder holder,
					MerchantCategoryListItem item, int position) {
				holder.bind(item, getActivityCoordinatorLayout(), shoppingListManager, endMerchant,
						getOnListItemOnClickListener(new ShoppingListItem(item, endMerchant),
								holder), position);
			}

			@Override
			public void onDataChanged() {
				endEmptyText.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
			}
		};
	}

	private View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item,
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

	private View getActivityCoordinatorLayout() {
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
		startLayoutManager.scrollToPositionWithOffset(startPositionIndex, startTopView);
		endLayoutManager.scrollToPositionWithOffset(endPositionIndex, endTopView);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_compare, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		if (savedInstanceState != null) {
			startPositionIndex = savedInstanceState.getInt(START_POS_INDEX);
			startTopView = savedInstanceState.getInt(START_TOP_VIEW);
			endPositionIndex = savedInstanceState.getInt(END_POS_INDEX);
			endTopView = savedInstanceState.getInt(END_TOP_VIEW);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (merchantMap != null && !merchantMap.isEmpty() && category != null &&
				!category.isEmpty()) {
			boolean startSet = false;
			for (Map.Entry<String, Merchant> entry : merchantMap.entrySet()) {
				if (!startSet) {
					startMerchantItemsDBRef = getDBRefForMerchantCategoryList(entry);
					startMerchant = entry.getValue();
					startSet = true;
				}
				else {
					endMerchantItemsDBRef = getDBRefForMerchantCategoryList(entry);
					endMerchant = entry.getValue();
				}
			}

			startLayoutManager = new LinearLayoutManager(getActivity());
			startLayoutManager.setReverseLayout(false);
			endLayoutManager = new LinearLayoutManager(getActivity());
			endLayoutManager.setReverseLayout(false);

			startItems.setLayoutManager(startLayoutManager);
			endItems.setLayoutManager(endLayoutManager);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (startItems != null && endItems != null) {
			startPositionIndex = startLayoutManager.findFirstVisibleItemPosition();
			endPositionIndex = endLayoutManager.findFirstVisibleItemPosition();
			View startView = startItems.getChildAt(0);
			View endView = endItems.getChildAt(0);

			startTopView =
					(startView == null) ? 0 : (startView.getTop() - startItems.getPaddingTop());
			endTopView = (endView == null) ? 0 : (endView.getTop() - endItems.getPaddingTop());

			outState.putInt(START_POS_INDEX, startPositionIndex);
			outState.putInt(START_TOP_VIEW, startTopView);
			outState.putInt(END_POS_INDEX, endPositionIndex);
			outState.putInt(END_TOP_VIEW, endTopView);
		}
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

}
