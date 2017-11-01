package hu.tvarga.capstone.cheaplist.ui.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.itemdetail.DetailContract;
import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.NutritionInformation;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.NutritionNameHelper.getNutritionLocalizedName;

public class DetailFragment extends DaggerFragment implements DetailContract.View {

	public static final String FRAGMENT_TAG = DetailFragment.class.getName();
	public static final String DETAIL_ITEM = "DETAIL_ITEM";

	@BindView(R.id.detailItemTitle)
	TextView detailItemTitle;

	@BindView(R.id.detailImage)
	ImageView detailImage;

	@BindView(R.id.detailPrice)
	TextView detailPrice;

	@BindView(R.id.detailPricePerUnit)
	TextView detailPricePerUnit;

	@BindView(R.id.detailNutritionInformation)
	LinearLayout detailNutritionInformation;

	@BindView(R.id.detailManufacturerInformation)
	TextView detailManufacturerInformation;

	@BindView(R.id.detailFab)
	FloatingActionButton fab;

	@Inject
	DetailContract.Presenter presenter;

	private ShoppingListItem itemFromArgument;
	private Unbinder unbinder;

	public static DetailFragment newInstance(ShoppingListItem item) {
		if (item == null) {
			return null;
		}
		Bundle arguments = new Bundle();
		arguments.putSerializable(DETAIL_ITEM, item);
		DetailFragment fragment = new DetailFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSharedElementEnterTransition(
				TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
		if (getArguments().containsKey(DETAIL_ITEM)) {
			itemFromArgument = (ShoppingListItem) getArguments().getSerializable(DETAIL_ITEM);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		presenter.onResume(this, itemFromArgument);
	}

	@Override
	public void onPause() {
		super.onPause();
		presenter.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	@NonNull
	private View.OnClickListener getAddToListOnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				presenter.addToShoppingList(itemFromArgument, itemFromArgument.merchant);
			}
		};
	}

	@Override
	public void updateUI(Item item) {
		detailItemTitle.setText(item.name);
		Picasso.with(getContext()).load(item.imageURL).placeholder(R.drawable.zzz_image).into(
				detailImage);
		detailPrice.setText(String.format("%s %s", item.price, item.currency));
		detailPricePerUnit.setText(
				String.format("%s %s %s", item.pricePerUnit, item.currency, item.unit));
		if (item.manufacturerInformation != null) {
			String manufacturerInformation = presenter.getManufacturerInformation(
					item.manufacturerInformation);
			String stringToShow = manufacturerInformation.length() == 0 ? getString(
					R.string.no_data_available) : manufacturerInformation;
			detailManufacturerInformation.setText(stringToShow);
		}
		if (item.nutritionInformation != null) {
			showNutritionInformation(item.nutritionInformation);
		}
	}

	@Override
	public void showFabAsRemove() {
		fab.setImageResource(R.drawable.zzz_playlist_minus);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				presenter.removeItemFromShoppingList();
			}
		});
	}

	@Override
	public void showFabAsAdd() {
		fab.setImageResource(R.drawable.zzz_playlist_plus);
		fab.setOnClickListener(getAddToListOnClickListener());
	}

	private void showNutritionInformation(NutritionInformation nutritionInformation) {
		detailNutritionInformation.removeAllViews();

		addEnergyAtTop(nutritionInformation.energy);

		Field[] fields = NutritionInformation.class.getDeclaredFields();
		for (Field field : fields) {
			Class<?> type = field.getType();
			String fieldName = field.getName();
			if (type == Double.class && !"energy".equals(fieldName)) {
				Double nutritionValue = null;
				try {
					nutritionValue = (Double) field.get(nutritionInformation);
				}
				catch (IllegalAccessException e) {
					Timber.d(e);
				}
				if (nutritionValue != null) {
					View view = LayoutInflater.from(getContext()).inflate(
							R.layout.nutrition_information_item, null);
					TextView name = view.findViewById(R.id.nutritionName);
					int nutritionLocalizedName = getNutritionLocalizedName(fieldName);
					if (nutritionLocalizedName == 0) {
						name.setText(fieldName);
					}
					else {
						name.setText(nutritionLocalizedName);
					}
					TextView value = view.findViewById(R.id.nutritionValue);
					String unit = getString(R.string.gram);
					value.setText(nutritionValue + unit);
					detailNutritionInformation.addView(view);
				}
			}
		}
	}

	private void addEnergyAtTop(Double energy) {
		if (energy != null) {
			View view = LayoutInflater.from(getContext()).inflate(
					R.layout.nutrition_information_item, null);
			TextView name = view.findViewById(R.id.nutritionName);
			name.setText(R.string.energy);
			TextView value = view.findViewById(R.id.nutritionValue);
			String unit = getString(R.string.kcal);
			value.setText(energy + unit);
			detailNutritionInformation.addView(view);
		}
	}

	@Override
	public void onDestroyView() {
		unbinder.unbind();
		super.onDestroyView();
	}

}
