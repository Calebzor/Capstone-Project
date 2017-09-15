package hu.tvarga.capstone.cheaplist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;

public class MerchantCategoryListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.name)
	TextView name;

	public MerchantCategoryListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public void bind(MerchantCategoryListItem item) {
		name.setText(item.name);
	}
}
