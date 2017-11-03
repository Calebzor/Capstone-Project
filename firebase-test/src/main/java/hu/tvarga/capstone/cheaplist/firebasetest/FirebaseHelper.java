package hu.tvarga.capstone.cheaplist.firebasetest;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;

public class FirebaseHelper {

	static final Log L = LogFactory.getLog(FirebaseHelper.class);
	public static final String MERCHANT_CATEGORY_LIST_ITEMS = "merchantCategoryListItems";
	public static final String ITEMS_DB_KEY = "items";
	public static final String MANUFACTURERS_DB_KEY = "manufacturers";

	static {
		BasicConfigurator.configure();
	}

	public static final String ANONYMOUS = "ANONYMOUS";
	private FirebaseDatabase firebaseDatabase;
	private ChildEventListener childEventListener;
	private FirebaseApp firebaseApp;
	private DatabaseReference items;
	private DatabaseReference merchants;
	private DatabaseReference manufacturers;
	private DatabaseReference publicReadable;

	public void initializeFirebaseApp() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(
				"x:\\cheaplist-916a2-firebase-adminsdk-79sqg-11459ab2f7.json");

		FirebaseOptions options = new FirebaseOptions.Builder().setCredential(
				FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl(
				"https://cheaplist-916a2.firebaseio.com").build();

		firebaseApp = FirebaseApp.initializeApp(options);

		firebaseDatabase = FirebaseDatabase.getInstance();

		publicReadable = firebaseDatabase.getReference().child("publicReadable");
		items = publicReadable.child(ITEMS_DB_KEY);
		merchants = publicReadable.child("merchants");
		manufacturers = publicReadable.child(MANUFACTURERS_DB_KEY);

		//		attachDatabaseReadListener();
	}

	public Task<Void> pushItemCategories() {
		DatabaseReference itemCategories = publicReadable.child("itemCategories");
		Map<String, Object> value = new HashMap<>();
		for (ItemCategory itemCategory : ItemCategory.values()) {
			String name = itemCategory.name();
			value.put(name, name);
		}

		return itemCategories.updateChildren(value);
	}

	public Task<Void> pushItem(Item item, Merchant merchant) {
		L.debug(String.format("item to be pushed: %s for merchant: %s", item, merchant));
		Map<String, Object> updatedItemData = new HashMap<>();
		if (item.id == null) {
			item.id = items.push().getKey();
		}
		if (item.manufacturerInformation != null) {
			if (item.manufacturerInformation.id == null) {
				item.manufacturerInformation.id = manufacturers.push().getKey();
			}
			item.manufacturerID = item.manufacturerInformation.id;
			updatedItemData.put(MANUFACTURERS_DB_KEY + "/" + item.manufacturerID,
					item.manufacturerInformation);
			updatedItemData.put("manufacturersItems" + "/" + item.manufacturerID + "/" + item.id,
					true);
		}
		updatedItemData.put(ITEMS_DB_KEY + "/" + item.id, item);
		updatedItemData.put(
				MERCHANT_CATEGORY_LIST_ITEMS + "/" + merchant.id + item.category + "/" + item.id,
				new MerchantCategoryListItem(item));
		return publicReadable.updateChildren(updatedItemData);
	}

	public Task<Void> pushMerchant(Merchant merchant) {
		String key = merchants.push().getKey();
		merchant.id = key;
		return merchants.child(key).setValue(merchant);
	}

	public void getMerchantsCategory(Merchant merchant, ValueEventListener listener) {
		getMerchantsCategory(merchant, ItemCategory.ALCOHOL, listener);
	}

	public void getMerchantsCategory(Merchant merchant, ItemCategory category,
			ValueEventListener listener) {
		publicReadable.child(MERCHANT_CATEGORY_LIST_ITEMS).child(merchant.id + category)
				.addValueEventListener(listener);
	}

	public void getItem(String itemID, DatabaseObjectCallback<Item> listener) {
		items.child(itemID).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Item item = snapshot.getValue(Item.class);
				manufacturers.child(item.manufacturerID).addValueEventListener(
						new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot snapshot) {
								item.manufacturerInformation = snapshot.getValue(
										ManufacturerInformation.class);
								listener.onDataChange(item);
							}

							@Override
							public void onCancelled(DatabaseError error) {
								listener.onCancelled(error);
							}
						});
			}

			@Override
			public void onCancelled(DatabaseError error) {
				listener.onCancelled(error);
			}
		});
	}

	public void getItem(ChildEventListener eventListener) {
		publicReadable.child(ITEMS_DB_KEY).addChildEventListener(eventListener);
	}

	private void attachDatabaseReadListener() {
		if (childEventListener == null) {
			childEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				}

				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String s) {

				}

				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) {

				}

				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String s) {

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			};
		}
		publicReadable.addChildEventListener(childEventListener);
	}

	public Task<UserRecord> createUser() {
		UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(
				"user@example.com").setEmailVerified(false).setPassword("secretPassword")
				.setPhoneNumber("+11234567890").setDisplayName("John Doe").setPhotoUrl(
						"http://www.example.com/12345678/photo.png").setDisabled(false);

		return FirebaseAuth.getInstance().createUser(request);
	}

	public void detachDatabaseReadListener() {
		if (childEventListener != null) {
			publicReadable.removeEventListener(childEventListener);
			childEventListener = null;
		}
		if (firebaseApp != null) {
			firebaseApp.delete();
		}
	}

	public void dropTables() {
		items.setValue(null);
		manufacturers.setValue(null);
		publicReadable.child("manufacturersItems").setValue(null);
		publicReadable.child("merchantCategoryListItems").setValue(null);
	}
}
