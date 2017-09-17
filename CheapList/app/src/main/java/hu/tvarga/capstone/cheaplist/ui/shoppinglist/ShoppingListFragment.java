package hu.tvarga.capstone.cheaplist.ui.shoppinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

public class ShoppingListFragment extends DaggerFragment {

	public interface ShoppingListItemClickAction {

		void onShoppingListItemClick(ShoppingListItem item);
	}

	@BindView(R.id.emptyText)
	TextView emptyText;

	@BindView(R.id.shoppingList)
	RecyclerView shoppingList;

	@Inject
	ShoppingListManager shoppingListManager;

	private Unbinder unbinder;
	private DatabaseReference dbRefForShoppingList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.shopping_list, container, false);
		unbinder = ButterKnife.bind(this, rootView);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		attachRecyclerViewAdapter();
	}

	private DatabaseReference getDBRefForShoppingList() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null) {
			return FirebaseDatabase.getInstance().getReference().child("userData").child(
					currentUser.getUid()).child("shoppingList");
		}
		return null;
	}

	private void attachRecyclerViewAdapter() {
		Timber.d("attachRecyclerViewAdapter");
		FirebaseRecyclerAdapter<ShoppingListItem, ShoppingListItemHolder> shoppingListAdapter =
				getShoppingListAdapter();

		shoppingList.setAdapter(shoppingListAdapter);
		shoppingList.setHasFixedSize(true);
		setUpSwipeAction();
	}

	//region Swipe action
	private void setUpSwipeAction() {
		ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
				0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
					RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
				if (viewHolder instanceof ShoppingListItemHolder) {
					ShoppingListItemHolder shoppingListItemHolder =
							(ShoppingListItemHolder) viewHolder;
					if (shoppingListItemHolder.item != null) {
						shoppingListManager.removeFromList(shoppingListItemHolder.item);
						View coordinatorLayout = getActivity().findViewById(R.id.coordinator);
						if (coordinatorLayout != null) {
							showSnackBar(coordinatorLayout, shoppingListItemHolder.item,
									shoppingListManager);
						}
					}
				}
			}
		};

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
		itemTouchHelper.attachToRecyclerView(shoppingList);
	}

	private void showSnackBar(View coordinatorLayout, final ShoppingListItem item,
			final ShoppingListManager shoppingListManager) {
		Snackbar.make(coordinatorLayout, R.string.removed_from_shopping_list, Snackbar.LENGTH_LONG)
				.setAction(R.string.undo, new View.OnClickListener() {
					@Override
					public void onClick(View undoView) {
						snackUndoAction(item, shoppingListManager);
					}
				}).setActionTextColor(
				ContextCompat.getColor(coordinatorLayout.getContext(), R.color.secondaryTextColor))
				.show();
	}

	private void snackUndoAction(ShoppingListItem item, ShoppingListManager shoppingListManager) {
		shoppingListManager.addToList(item);
	}
	//endregion

	protected FirebaseRecyclerAdapter<ShoppingListItem, ShoppingListItemHolder> getShoppingListAdapter() {
		if (dbRefForShoppingList == null) {
			return null;
		}
		Query query = dbRefForShoppingList.orderByChild("checked");
		return new FirebaseRecyclerAdapter<ShoppingListItem, ShoppingListItemHolder>(
				ShoppingListItem.class, R.layout.shopping_list_item, ShoppingListItemHolder.class,
				query, this) {
			@Override
			public void populateViewHolder(ShoppingListItemHolder holder, ShoppingListItem item,
					int position) {
				ShoppingListActivity shoppingListActivity = (ShoppingListActivity) getActivity();
				if (shoppingListActivity != null && !shoppingListActivity.hasDetailFragment()) {
					shoppingListActivity.addDetailFragment(item);
				}
				holder.bind(item, shoppingListManager, getOnListItemOnClickListener(item));
			}

			@Override
			public void onDataChanged() {
				shoppingList.setVisibility(getItemCount() != 0 ? View.VISIBLE : View.GONE);
				emptyText.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
			}
		};
	}

	private View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((ShoppingListItemClickAction) getActivity()).onShoppingListItemClick(item);
			}
		};
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		dbRefForShoppingList = getDBRefForShoppingList();

		LinearLayoutManager startLayoutManager = new LinearLayoutManager(getActivity());
		startLayoutManager.setReverseLayout(false);

		shoppingList.setLayoutManager(startLayoutManager);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

}
