package hu.tvarga.capstone.cheaplist.business.compare.comparesettings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.UserService;
import hu.tvarga.capstone.cheaplist.business.compare.CompareService;
import hu.tvarga.capstone.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectListener;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectReceiver;
import hu.tvarga.capstone.cheaplist.utility.broadcast.ObjectReceiverFactory;
import timber.log.Timber;

public class CompareSettingsPresenter implements CompareSettingsContract.Presenter {

	private final CompareService compareService;
	private final UserService userService;
	private final ObjectReceiver<CompareSettingsFilterChangedBroadcastObject>
			compareSettingsFilterChangedBroadcastObjectObjectReceiver;
	private CompareSettingsContract.View view;

	@SuppressWarnings("FieldCanBeLocal")
	private ObjectListener<CompareSettingsFilterChangedBroadcastObject> filterChangeBroadcast =
			new ObjectListener<CompareSettingsFilterChangedBroadcastObject>() {
				@Override
				public void onReceive(CompareSettingsFilterChangedBroadcastObject object) {
					filterChanged();
				}

				@Override
				public void onFailure(Throwable throwable) {
					Timber.e("filterChangeBroadcast#onFailure", throwable);
				}

				@Override
				public void onCancelled() {
					Timber.d("filterChangeBroadcast#onCancelled");
				}
			};
	private RecyclerView.Adapter<CompareSettingsCategoryHolder> adapter;

	@Inject
	public CompareSettingsPresenter(CompareService compareService, UserService userService,
			ObjectReceiverFactory objectReceiverFactory) {
		view = new DialogSettingsViewStub();
		compareSettingsFilterChangedBroadcastObjectObjectReceiver = objectReceiverFactory.get(
				filterChangeBroadcast, CompareSettingsFilterChangedBroadcastObject.class);
		this.compareService = compareService;
		this.userService = userService;
	}

	@Override
	public void onStart(CompareSettingsContract.View view) {
		this.view = view;
		compareSettingsFilterChangedBroadcastObjectObjectReceiver.register();

	}

	@Override
	public void onStop() {
		compareSettingsFilterChangedBroadcastObjectObjectReceiver.unregister();
		view = new DialogSettingsViewStub();
	}

	@Override
	public RecyclerView.Adapter<CompareSettingsCategoryHolder> getCategoriesFilterForUserAdapter() {
		if (adapter == null) {
			adapter = new RecyclerView.Adapter<CompareSettingsCategoryHolder>() {
				@Override
				public CompareSettingsCategoryHolder onCreateViewHolder(ViewGroup parent,
						int viewType) {
					View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
							R.layout.category_filter_list_item, parent, false);
					return new CompareSettingsCategoryHolder(viewHolder);
				}

				@Override
				public void onBindViewHolder(CompareSettingsCategoryHolder holder, int position) {
					Map<String, Boolean> categoriesFilterForUser =
							userService.getCategoriesFilterForUser();
					final String category = compareService.getCategories().get(position);
					boolean filterSelected = false;

					if (categoriesFilterForUser.containsKey(category)) {
						filterSelected = categoriesFilterForUser.get(category);
					}
					holder.bind(category, filterSelected, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Map<String, Boolean> newFilter = new HashMap<>();
							for (String categoryInList : compareService.getCategories()) {
								newFilter.put(categoryInList, categoryInList.equals(category));
							}
							userService.setCategoriesFilterForUser(newFilter);
						}
					});
				}

				@Override
				public int getItemCount() {
					return compareService.getCategories().size();
				}
			};
		}
		return adapter;
	}

	@SuppressWarnings("squid:S3398")
	private void filterChanged() {
		adapter.notifyDataSetChanged();
		Map<String, Boolean> categoriesFilterForUser = userService.getCategoriesFilterForUser();
		Set<Map.Entry<String, Boolean>> entries = categoriesFilterForUser.entrySet();

		for (Map.Entry<String, Boolean> entry : entries) {
			if (entry.getValue()) {
				compareService.setCategory(entry.getKey());
				break;
			}
		}
		compareService.getData();
	}
}
