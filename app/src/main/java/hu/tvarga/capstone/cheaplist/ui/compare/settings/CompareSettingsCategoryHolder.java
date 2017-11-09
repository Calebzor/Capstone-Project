package hu.tvarga.capstone.cheaplist.ui.compare.settings;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;

public class CompareSettingsCategoryHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.categoryListItemElement)
	AppCompatCheckBox categoryListItemElement;

	public CompareSettingsCategoryHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public void bind(ItemCategory category, boolean filterSelected,
			View.OnClickListener onClickListener) {
		categoryListItemElement.setText(category.toString());
		categoryListItemElement.setChecked(filterSelected);
		categoryListItemElement.setOnClickListener(onClickListener);
	}
}
