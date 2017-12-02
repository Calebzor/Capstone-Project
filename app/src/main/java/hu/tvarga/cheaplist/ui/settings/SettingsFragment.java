package hu.tvarga.cheaplist.ui.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.compare.settings.SettingsContract;
import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.dao.UserSetting;
import hu.tvarga.cheaplist.ui.MainActivity;
import hu.tvarga.cheaplist.ui.TitleSettingFragment;

public class SettingsFragment extends DaggerFragment implements TitleSettingFragment {

	public static final String FRAGMENT_TAG = SettingsFragment.class.getName();

	@BindView(R.id.settingsList)
	RecyclerView settingsList;

	@Inject
	UserService userService;

	@Inject
	SettingsContract.Presenter presenter;

	private Unbinder unbinder;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		((MainActivity) getActivity()).setTitle(this);
		RecyclerView.Adapter<SettingsListItemHolder> adapter = getAdapter();
		settingsList.setAdapter(adapter);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	public RecyclerView.Adapter<SettingsListItemHolder> getAdapter() {
		return new RecyclerView.Adapter<SettingsListItemHolder>() {
			@Override
			public SettingsListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View viewHolder = LayoutInflater.from(getContext()).inflate(
						R.layout.settings_list_item, parent, false);
				return new SettingsListItemHolder(viewHolder);
			}

			@Override
			public void onBindViewHolder(SettingsListItemHolder holder, int position) {
				UserSetting userSetting = userService.getUserSettings().get(position);
				View.OnClickListener onClickListener = getOnClickListener(userSetting);
				holder.bind(userSetting, onClickListener);
			}

			@Override
			public int getItemCount() {
				return userService.getUserSettings().size();
			}
		};
	}

	@NonNull
	private View.OnClickListener getOnClickListener(final UserSetting userSetting) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				presenter.onSettingsClicked(userSetting);
			}
		};
	}

	@Override
	public int getTitle() {
		return R.string.settings;
	}
}
