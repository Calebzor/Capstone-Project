package hu.tvarga.cheaplist.ui.settings;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

class SettingsListItemHolder extends RecyclerView.ViewHolder {

	SettingsListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
