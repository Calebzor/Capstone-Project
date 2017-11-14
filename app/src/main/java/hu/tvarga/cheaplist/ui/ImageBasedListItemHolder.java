package hu.tvarga.cheaplist.ui;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;

public abstract class ImageBasedListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.image)
	protected AppCompatImageView image;

	protected ImageBasedListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
