package hu.tvarga.capstone.cheaplist.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.capstone.cheaplist.dao.ShoppingListItem;
import timber.log.Timber;

import static hu.tvarga.capstone.cheaplist.ui.detail.DetailActivity.DETAIL_ITEM;

public class DetailFragment extends DaggerFragment {

	@BindView(R.id.detailItemTitle)
	TextView detailItemTitle;

	@BindView(R.id.detailImage)
	ImageView detailImage;

	@BindView(R.id.detailPrice)
	TextView detailPrice;

	@BindView(R.id.detailPricePerUnit)
	TextView detailPricePerUnit;

	@BindView(R.id.detailNutritionInformation)
	RecyclerView detailNutritionInformation;

	@BindView(R.id.detailManufacturerInformation)
	TextView detailManufacturerInformation;

	@BindView(R.id.detailFab)
	FloatingActionButton fab;

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

	private String itemID;
	private Unbinder unbinder;

	public static DetailFragment newInstance(String itemID) {
		Bundle arguments = new Bundle();
		arguments.putString(DETAIL_ITEM, itemID);
		DetailFragment fragment = new DetailFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		if (getArguments().containsKey(DETAIL_ITEM)) {
			itemID = getArguments().getString(DETAIL_ITEM);
		}
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

	private void loadData() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null) {
			shoppingListItemEventListener = new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					ShoppingListItem shoppingListItem = dataSnapshot.getValue(
							ShoppingListItem.class);
					Timber.d("listItemChange %s", shoppingListItem);
					if (shoppingListItem != null) {
						fab.setImageResource(R.drawable.zzz_playlist_minus);
					}
					else {
						fab.setImageResource(R.drawable.zzz_playlist_plus);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			};
			shoppingListItemRef = FirebaseDatabase.getInstance().getReference().child("userData")
					.child(currentUser.getUid()).child("shoppingList").child(itemID);
			shoppingListItemRef.addValueEventListener(shoppingListItemEventListener);
			itemRef = FirebaseDatabase.getInstance().getReference().child("publicReadable").child(
					"items").child(itemID);
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

				}
			};
			itemRef.addValueEventListener(itemEventListener);
		}
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
			setUpNutritionInformation(item);
		}
	}

	private void setUpNutritionInformation(Item item) {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		shoppingListItemRef.removeEventListener(shoppingListItemEventListener);
		itemRef.removeEventListener(itemEventListener);
	}

}
