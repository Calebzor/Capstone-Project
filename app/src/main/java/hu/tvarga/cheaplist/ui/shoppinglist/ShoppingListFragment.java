package hu.tvarga.cheaplist.ui.shoppinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListContract;
import hu.tvarga.cheaplist.dao.ShoppingListItem;
import hu.tvarga.cheaplist.ui.MainActivity;
import hu.tvarga.cheaplist.ui.TitleSettingFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShoppingListFragment extends DaggerFragment
		implements ShoppingListContract.View, TitleSettingFragment {

	public static final String FRAGMENT_TAG = ShoppingListFragment.class.getName();

	@BindView(R.id.emptyText)
	TextView emptyText;

	@BindView(R.id.shoppingList)
	RecyclerView shoppingList;

	@Inject
	ShoppingListContract.Presenter shoppingListPresenter;

	private Unbinder unbinder;

	public static ShoppingListFragment newInstance() {
		return new ShoppingListFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		shoppingListPresenter.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		shoppingListPresenter.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.shopping_list, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	//region Swipe action
	private void setUpSwipeAction() {
		ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ShoppingListItemTouchHelper(0,
				ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
				new ShoppingListItemTouchHelper.ShoppingListItemTouchHelperListener() {
					@Override
					public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
						ShoppingListItemHolder shoppingListItemHolder =
								(ShoppingListItemHolder) viewHolder;
						if (shoppingListItemHolder.item != null) {
							shoppingListPresenter.removeFromList(shoppingListItemHolder.item);
							View coordinatorLayout = getActivity().findViewById(R.id.coordinator);
							if (coordinatorLayout != null) {
								showSnackBar(coordinatorLayout, shoppingListItemHolder.item);
							}
						}
					}
				});

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
		itemTouchHelper.attachToRecyclerView(shoppingList);
	}

	private void showSnackBar(View coordinatorLayout, final ShoppingListItem item) {
		Snackbar.make(coordinatorLayout, R.string.removed_from_shopping_list, Snackbar.LENGTH_LONG)
				.setAction(R.string.undo, new View.OnClickListener() {
					@Override
					public void onClick(View undoView) {
						snackUndoAction(item);
					}
				}).setActionTextColor(
				ContextCompat.getColor(coordinatorLayout.getContext(), R.color.secondaryTextColor))
				.show();
	}

	private void snackUndoAction(ShoppingListItem item) {
		shoppingListPresenter.addToList(item);
	}
	//endregion

	@Override
	public void setEmptyView(int itemCount) {
		shoppingList.setVisibility(itemCount != 0 ? VISIBLE : GONE);
		emptyText.setVisibility(itemCount == 0 ? VISIBLE : GONE);
	}

	@Override
	public View.OnClickListener getOnListItemOnClickListener(final ShoppingListItem item,
			final ShoppingListItemHolder holder) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				((MainActivity) getActivity()).openDetailView(item, holder);
			}
		};
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		((MainActivity) getActivity()).setTitle(this);
		RecyclerView.Adapter<ShoppingListItemHolder> adapter = getAdapter();
		shoppingListPresenter.setAdapter(adapter);
		shoppingList.setAdapter(adapter);
		setUpSwipeAction();
		super.onViewCreated(view, savedInstanceState);
	}

	private RecyclerView.Adapter<ShoppingListItemHolder> getAdapter() {
		return new RecyclerView.Adapter<ShoppingListItemHolder>() {
			@Override
			public ShoppingListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View viewHolder = LayoutInflater.from(getContext()).inflate(
						R.layout.shopping_list_item, parent, false);
				return new ShoppingListItemHolder(viewHolder);
			}

			@Override
			public void onBindViewHolder(ShoppingListItemHolder holder, int position) {
				shoppingListPresenter.adapterOnBindViewHolder(holder, position);
			}

			@Override
			public int getItemCount() {
				return shoppingListPresenter.adapterGetItemCount();
			}
		};
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public int getTitle() {
		return R.string.shopping_list_menu_item;
	}
}
