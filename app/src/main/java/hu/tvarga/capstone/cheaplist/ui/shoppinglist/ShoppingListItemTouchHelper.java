package hu.tvarga.capstone.cheaplist.ui.shoppinglist;

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
		shoppingListItemHolder.deleteIconEnd.setVisibility(dX > 0 ? GONE : VISIBLE);
		shoppingListItemHolder.deleteTextEnd.setVisibility(dX > 0 ? GONE : VISIBLE);
		shoppingListItemHolder.deleteIconStart.setVisibility(dX < 0 ? GONE : VISIBLE);
		shoppingListItemHolder.deleteTextStart.setVisibility(dX < 0 ? GONE : VISIBLE);

		getDefaultUIUtil().onDraw(c, recyclerView, shoppingListItemHolder.viewForeground, dX, dY,
				actionState, isCurrentlyActive);
	}

	interface ShoppingListItemTouchHelperListener {

		void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
	}
}
