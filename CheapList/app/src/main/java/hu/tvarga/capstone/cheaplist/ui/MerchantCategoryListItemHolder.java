package hu.tvarga.capstone.cheaplist.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;

public class MerchantCategoryListItemHolder extends RecyclerView.ViewHolder {

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

	public void bind(MerchantCategoryListItem item) {
		name.setText(item.name != null ? item.name.trim() : null);
		price.setText(String.format("%s %s", item.price, item.currency));
		pricePerUnit.setText(
				String.format("%s %s %s", item.pricePerUnit, item.currency, item.unit));
		Picasso.with(itemView.getContext()).load(item.imageURL).into(image);
	}
}
