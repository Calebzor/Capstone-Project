package hu.tvarga.cheaplist.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;

public abstract class ImageBasedListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.image)
	protected ImageView image;

	protected ImageBasedListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
