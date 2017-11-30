package hu.tvarga.cheaplist.business.compare.settings;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import hu.tvarga.cheaplist.business.compare.CompareService;
import hu.tvarga.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;
import hu.tvarga.cheaplist.utility.eventbus.Event;

public class CompareSettingsPresenter implements CompareSettingsContract.Presenter {

	private final CompareService compareService;
	private final UserService userService;
	private final Event event;

	RecyclerView.Adapter<CompareSettingsCategoryHolder> adapter;

	@Inject
	public CompareSettingsPresenter(CompareService compareService, UserService userService,
			Event event) {
		this.compareService = compareService;
		this.userService = userService;
		this.event = event;
	}

	@Override
	public void onStart() {
		event.register(this);
	}

	@Override
	public void onStop() {
		event.unregister(this);
	}

	@Subscribe
	public void handleCompareSettingsFilterChangedBroadcastObject(
			CompareSettingsFilterChangedBroadcastObject object) {
		filterChanged(object);
	}

	@Override
	public RecyclerView.Adapter<CompareSettingsCategoryHolder> getCategoriesFilterForUserAdapter() {
		if (adapter == null) {
			adapter = new CompareSettingsAdapter(userService, compareService);
		}
		return adapter;
	}

	@SuppressWarnings("squid:S3398")
	private void filterChanged(final CompareSettingsFilterChangedBroadcastObject object) {

		compareService.setCategoryFilter(object.getNewFilter());
		if (object.getOldFilter().isEmpty()) {
			adapter.notifyDataSetChanged();
		}
		else {
			DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
				@Override
				public int getOldListSize() {
					return object.getOldFilter().size();
				}

				@Override
				public int getNewListSize() {
					return object.getNewFilter().size();
				}

				@Override
				public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
					return object.getOldFilter().get(oldItemPosition).category ==
							object.getNewFilter().get(newItemPosition).category;
				}

				@Override
				public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
					UserCategoryFilterListItem oldItem = object.getOldFilter().get(oldItemPosition);
					UserCategoryFilterListItem newItem = object.getNewFilter().get(newItemPosition);
					return oldItem.category == newItem.category &&
							oldItem.checked == newItem.checked;
				}
			});
			result.dispatchUpdatesTo(adapter);
		}

		compareService.getData();
	}
}
