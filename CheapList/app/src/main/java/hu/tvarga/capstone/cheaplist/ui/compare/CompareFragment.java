package hu.tvarga.capstone.cheaplist.ui.compare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class CompareFragment extends DaggerFragment {

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

	@Inject
	ShoppingListManager shoppingListManager;

	private String category;
	private Unbinder unbinder;
	private HashMap<String, Merchant> merchantMap;
	protected DatabaseReference startMerchantItemsDBRef;
	protected DatabaseReference endMerchantItemsDBRef;
	private Merchant startMerchant;
	private Merchant endMerchant;

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
						getOnListItemOnClickListener(new ShoppingListItem(item, startMerchant)));
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
						getOnListItemOnClickListener(new ShoppingListItem(item, endMerchant)));
			}

			@Override
			public void onDataChanged() {
				endEmptyText.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
			}
		};
	}

	private View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Class<?> targetActivity = DetailActivity.class;
				if (getResources().getBoolean(multipane)) {
					targetActivity = ShoppingListActivity.class;
				}
				Intent intent = new Intent(getContext(), targetActivity);
				intent.putExtra(DETAIL_ITEM, item);
				startActivity(intent);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_compare, container, false);
		unbinder = ButterKnife.bind(this, rootView);

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

			LinearLayoutManager startLayoutManager = new LinearLayoutManager(getActivity());
			startLayoutManager.setReverseLayout(false);
			LinearLayoutManager endLayoutManager = new LinearLayoutManager(getActivity());
			endLayoutManager.setReverseLayout(false);

			startItems.setLayoutManager(startLayoutManager);
			endItems.setLayoutManager(endLayoutManager);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

}
