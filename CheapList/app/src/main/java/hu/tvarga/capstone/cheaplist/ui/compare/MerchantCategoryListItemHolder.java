package hu.tvarga.capstone.cheaplist.ui.compare;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;

public class MerchantCategoryListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.itemContainer)
	View itemContainer;

	@BindView(R.id.name)
	TextView name;

	@BindView(R.id.price)
	TextView price;

	@BindView(R.id.pricePerUnit)
	TextView pricePerUnit;

	@BindView(R.id.image)
	ImageView image;

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
		Picasso.with(itemView.getContext()).load(item.imageURL).into(image);

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
