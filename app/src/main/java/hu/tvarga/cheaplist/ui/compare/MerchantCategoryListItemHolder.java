package hu.tvarga.cheaplist.ui.compare;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.MerchantCategoryListItem;
import hu.tvarga.cheaplist.ui.ImageBasedListItemHolder;

import static hu.tvarga.cheaplist.utility.SharedElementTransition.setTransitionName;

public class MerchantCategoryListItemHolder extends ImageBasedListItemHolder {

	@BindView(R.id.itemContainer)
	View itemContainer;

	@BindView(R.id.name)
	TextView name;

	@BindView(R.id.price)
	TextView price;

	@BindView(R.id.pricePerUnit)
	TextView pricePerUnit;

	public MerchantCategoryListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public void bind(final MerchantCategoryListItem item, final View coordinatorLayout,
			final ShoppingListManager shoppingListManager, final Merchant merchant,
			View.OnClickListener onClickListener) {
		name.setText(item.name != null ? item.name.trim() : null);
		price.setText(String.format("%s %s", item.price, item.currency));
		pricePerUnit.setText(
				String.format("%s %s %s", item.pricePerUnit, item.currency, item.unit));
		String imageUrl = item.getThumbnail();
		loadImage(imageUrl);

		setTransitionName(image, merchant, item.id);

		itemContainer.setOnClickListener(onClickListener);

		itemContainer.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				shoppingListManager.addToList(item, merchant);
				if (coordinatorLayout != null) {
					showSnackBar(view, coordinatorLayout, item, shoppingListManager);
				}
				return true;
			}
		});
	}

	private void showSnackBar(View view, View coordinatorLayout,
			final MerchantCategoryListItem item, final ShoppingListManager shoppingListManager) {
		Snackbar.make(coordinatorLayout, R.string.added_to_shopping_list, Snackbar.LENGTH_LONG)
				.setAction(R.string.undo, new View.OnClickListener() {
					@Override
					public void onClick(View undoView) {
						snackUndoAction(item, shoppingListManager);
					}
				}).setActionTextColor(
				ContextCompat.getColor(view.getContext(), R.color.secondaryTextColor)).show();
	}

	private void snackUndoAction(MerchantCategoryListItem item,
			ShoppingListManager shoppingListManager) {
		shoppingListManager.removeFromList(item);
	}
}
