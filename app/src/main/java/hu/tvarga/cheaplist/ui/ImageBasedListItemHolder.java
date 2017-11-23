package hu.tvarga.cheaplist.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.user.UserService;

public abstract class ImageBasedListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.image)
	protected AppCompatImageView image;

	protected ImageBasedListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	protected void loadImage(String imageUrl) {
		if (UserService.shouldDownloadImages()) {
			Glide.with(itemView.getContext()).load(imageUrl).apply(
					new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)).into(
					this.image);
		}
		else {
			image.setImageDrawable(
					ContextCompat.getDrawable(image.getContext(), R.drawable.image_placeholder));
		}
	}
}
