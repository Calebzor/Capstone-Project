package hu.tvarga.cheaplist.ui.settings;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.user.UserSettingStringsMap;
import hu.tvarga.cheaplist.dao.UserSetting;

public class SettingsListItemHolder extends RecyclerView.ViewHolder {

	@BindView(R.id.settingsCheckbox)
	AppCompatCheckBox checkBox;

	@BindView(R.id.settingsDescription)
	TextView description;

	SettingsListItemHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public void bind(UserSetting userSetting, View.OnClickListener onClickListener) {
		checkBox.setChecked((Boolean) userSetting.value);
		checkBox.setOnClickListener(onClickListener);
		UserSettingStringsMap userSettingStringsMap = UserSettingStringsMap.valueOf(
				userSetting.name);
		checkBox.setText(userSettingStringsMap.getName());
		description.setText(userSettingStringsMap.getDescription());
	}
}
