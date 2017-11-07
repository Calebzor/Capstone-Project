package hu.tvarga.capstone.cheaplist.ui;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.Merchant;

public abstract class ImageBasedListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.image)
	protected ImageView image;

	protected ImageBasedListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	protected void setTransitionName(Merchant merchant, String key) {
		ViewCompat.setTransitionName(image, key + merchant.name + "_itemImage");
	}
}
