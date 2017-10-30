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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.ShoppingListManager;
import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.capstone.cheaplist.dao.NutritionInformation;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.NutritionNameHelper.getNutritionLocalizedName;
import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;

public class DetailFragment extends DaggerFragment {

	public static final String FRAGMENT_TAG = DetailFragment.class.getName();

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
	ShoppingListManager shoppingListManager;

	private DatabaseReference itemRef;
	private ValueEventListener itemEventListener;
	private DatabaseReference shoppingListItemRef;
	private ValueEventListener shoppingListItemEventListener;

	private String getManufacturerInformation(ManufacturerInformation manufacturerInformation) {
		StringBuilder sb = new StringBuilder();
		if (manufacturerInformation.address != null) {
			sb.append(manufacturerInformation.address).append("\n");
		}
		if (manufacturerInformation.contact != null) {
			sb.append(manufacturerInformation.contact).append("\n");
		}
		if (manufacturerInformation.supplier != null) {
			sb.append(manufacturerInformation.supplier).append("\n");
		}
		String string = sb.toString();
		return string.length() == 0 ? getString(R.string.no_data_available) : string;
	}

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

	public void setItemFromArgument(ShoppingListItem itemFromArgument) {
		this.itemFromArgument = itemFromArgument;
	}

	@Override
	public void onStart() {
		super.onStart();

		loadData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		return rootView;
	}

	public void loadData() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null) {
			shoppingListItemEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					final ShoppingListItem shoppingListItem = dataSnapshot.getValue(
							ShoppingListItem.class);
					Timber.d("listItemChange %s", shoppingListItem);
					if (fab != null) {
						if (shoppingListItem != null) {
							fab.setImageResource(R.drawable.zzz_playlist_minus);
							fab.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									shoppingListManager.removeFromList(shoppingListItem);
								}
							});
						}
						else {
							fab.setImageResource(R.drawable.zzz_playlist_plus);
							fab.setOnClickListener(getAddToListOnClickListener());
						}
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Timber.d("shoppingListItemEventListener#onCancelled %s", databaseError);
				}
			};
			shoppingListItemRef = FirebaseDatabase.getInstance().getReference().child("userData")
					.child(currentUser.getUid()).child("shoppingList").child(itemFromArgument.id);
			shoppingListItemRef.addValueEventListener(shoppingListItemEventListener);
			itemRef = FirebaseDatabase.getInstance().getReference().child("publicReadable").child(
					"items").child(itemFromArgument.id);
			itemEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					Item item = dataSnapshot.getValue(Item.class);
					Timber.d("items %s", item);
					if (item != null) {
						updateUI(item);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Timber.d("itemEventListener#onCancelled %s", databaseError);
				}
			};
			itemRef.addValueEventListener(itemEventListener);
		}
	}

	@NonNull
	private View.OnClickListener getAddToListOnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				shoppingListManager.addToList(itemFromArgument, itemFromArgument.merchant);
			}
		};
	}

	private void updateUI(Item item) {
		detailItemTitle.setText(item.name);
		Picasso.with(getContext()).load(item.imageURL).placeholder(R.drawable.zzz_image).into(
				detailImage);
		detailPrice.setText(String.format("%s %s", item.price, item.currency));
		detailPricePerUnit.setText(
				String.format("%s %s %s", item.pricePerUnit, item.currency, item.unit));
		if (item.manufacturerInformation != null) {
			detailManufacturerInformation.setText(
					getManufacturerInformation(item.manufacturerInformation));
		}
		if (item.nutritionInformation != null) {
			showNutritionInformation(item.nutritionInformation);
		}
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
		if (shoppingListItemRef != null) {
			shoppingListItemRef.removeEventListener(shoppingListItemEventListener);
		}
		if (itemRef != null) {
			itemRef.removeEventListener(itemEventListener);
		}
		super.onDestroyView();
	}

}
