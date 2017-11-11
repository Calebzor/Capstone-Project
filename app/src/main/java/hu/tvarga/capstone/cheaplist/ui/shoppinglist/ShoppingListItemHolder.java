package hu.tvarga.capstone.cheaplist.ui.shoppinglist;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import hu.tvarga.capstone.cheaplist.ui.ImageBasedListItemHolder;

public class ShoppingListItemHolder extends ImageBasedListItemHolder {

	@BindView(R.id.itemContainer)
	View itemContainer;

	@BindView(R.id.name)
	TextView name;

	@BindView(R.id.price)
	TextView price;

	@BindView(R.id.pricePerUnit)
	TextView pricePerUnit;

	@BindView(R.id.shoppingListItemCheckBox)
	AppCompatCheckBox shoppingListItemCheckBox;

	@BindView(R.id.view_background)
	RelativeLayout viewBackground;

	@BindView(R.id.view_foreground)
	RelativeLayout viewForeground;

	@BindView(R.id.delete_iconEnd)
	View deleteIconEnd;
	@BindView(R.id.delete_textEnd)
	View deleteTextEnd;
	@BindView(R.id.delete_iconStart)
	View deleteIconStart;
	@BindView(R.id.delete_textStart)
	View deleteTextStart;

	ShoppingListItem item;

	ShoppingListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public void bind(final ShoppingListItem item, final ShoppingListManager shoppingListManager,
			View.OnClickListener onClickListener) {
		this.item = item;
		name.setText(item.name != null ? item.name.trim() : null);
		price.setText(String.format("%s %s", item.price, item.currency));
		pricePerUnit.setText(
				String.format("%s %s %s", item.pricePerUnit, item.currency, item.unit));
		Glide.with(itemView.getContext()).load(item.imageURL).apply(
				new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)).into(
				image);
		shoppingListItemCheckBox.setChecked(item.checked);

		// would probably not need merchant name for uniqueness here
		setTransitionName(item.merchant, item.id);

		itemContainer.setOnClickListener(onClickListener);

		itemContainer.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				checkAction(item, shoppingListManager);
				return true;
			}
		});
		shoppingListItemCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkAction(item, shoppingListManager);
			}
		});
	}

	private void checkAction(ShoppingListItem item, ShoppingListManager shoppingListManager) {
		if (item.checked) {
			shoppingListManager.unCheckItem(item);
		}
		else {
			shoppingListManager.checkItem(item);

		}
	}

}
