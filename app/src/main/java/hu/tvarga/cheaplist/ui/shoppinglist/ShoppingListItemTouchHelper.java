package hu.tvarga.cheaplist.ui.shoppinglist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShoppingListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

	private final ShoppingListItemTouchHelperListener shoppingListItemTouchHelperListener;

	ShoppingListItemTouchHelper(int dragDirs, int swipeDirs,
			ShoppingListItemTouchHelperListener shoppingListItemTouchHelperListener) {
		super(dragDirs, swipeDirs);
		this.shoppingListItemTouchHelperListener = shoppingListItemTouchHelperListener;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
			RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
		if (viewHolder instanceof ShoppingListItemHolder) {
			shoppingListItemTouchHelperListener.onSwiped(viewHolder, swipeDir);
		}
	}

	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		if (viewHolder != null) {
			View foregroundView = ((ShoppingListItemHolder) viewHolder).viewForeground;
			getDefaultUIUtil().onSelected(foregroundView);
		}
	}

	@Override
	public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
			RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
			boolean isCurrentlyActive) {
		View foregroundView = ((ShoppingListItemHolder) viewHolder).viewForeground;
		getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState,
				isCurrentlyActive);
	}

	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		View foregroundView = ((ShoppingListItemHolder) viewHolder).viewForeground;
		getDefaultUIUtil().clearView(foregroundView);
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
			float dX, float dY, int actionState, boolean isCurrentlyActive) {
		ShoppingListItemHolder shoppingListItemHolder = (ShoppingListItemHolder) viewHolder;
		setVisibilityForEnd(shoppingListItemHolder.deleteIconEnd, dX);
		setVisibilityForEnd(shoppingListItemHolder.deleteTextEnd, dX);
		setVisibilityForStart(shoppingListItemHolder.deleteIconStart, dX);
		setVisibilityForStart(shoppingListItemHolder.deleteTextStart, dX);

		getDefaultUIUtil().onDraw(c, recyclerView, shoppingListItemHolder.viewForeground, dX, dY,
				actionState, isCurrentlyActive);
	}

	private void setVisibilityForStart(View view, float dX) {
		view.setVisibility(dX < 0 ? GONE : VISIBLE);
	}

	private void setVisibilityForEnd(View view, float dX) {
		view.setVisibility(dX > 0 ? GONE : VISIBLE);
	}

	interface ShoppingListItemTouchHelperListener {

		void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
	}
}
