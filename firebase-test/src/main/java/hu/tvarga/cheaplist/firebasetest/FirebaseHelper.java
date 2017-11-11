package hu.tvarga.cheaplist.firebasetest;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
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

import hu.tvarga.cheaplist.dao.Item;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.MerchantCategoryListItem;

import static hu.tvarga.cheaplist.dao.ItemCategory.ALCOHOL;

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
	private DocumentReference storePublicReadable;

	private Firestore store;
	private CollectionReference storeMerchants;

	public void initializeFirebaseApp() throws IOException {
		//		FileInputStream serviceAccount = new FileInputStream(
		//				"x:\\cheaplist-916a2-firebase-adminsdk-79sqg-11459ab2f7.json");
		//
		//		FirebaseOptions options = new FirebaseOptions.Builder().setCredential(
		//				FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl(
		//				"https://cheaplist-916a2.firebaseio.com").build();

		//		firebaseApp = FirebaseApp.initializeApp(options);

		//		firebaseDatabase = FirebaseDatabase.getInstance();

		//		publicReadable = firebaseDatabase.getReference().child("publicReadable");
		//		items = publicReadable.child(ITEMS_DB_KEY);
		//		merchants = publicReadable.child("merchants");
		//		manufacturers = publicReadable.child(MANUFACTURERS_DB_KEY);

		FileInputStream serviceAccountFirestore = new FileInputStream(
				"x:\\CheapList-3aada05c9fab.json");
		GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountFirestore);
		FirebaseOptions firebaseOptions = new FirebaseOptions.Builder().setCredentials(credentials)
				.setProjectId("cheaplist-916a2").build();
		FirebaseApp.initializeApp(firebaseOptions);

		store = FirestoreClient.getFirestore();
		storeMerchants = store.collection("merchants");

		//		attachDatabaseReadListener();
	}

	public ApiFuture<?> pushItemCategories() {
		CollectionReference itemCategories = store.collection("itemCategories");
		for (ItemCategory itemCategory : ItemCategory.values()) {
			String name = itemCategory.name();
			Map<String, String> category = new HashMap<>();
			category.put("name", name);
			itemCategories.add(category);
			//			itemCategories.document(name).set(category);
		}
		return null;
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

	public ApiFuture<?> pushItemToStore(Item item, Merchant merchant) {
		L.debug(String.format("item to be pushed: %s for merchant: %s", item, merchant));
		//		item.category = ItemCategory.MEAT;
		CollectionReference merchantCategoryListItems = store.collection("merchantItems").document(
				merchant.id).collection("items");
		DocumentReference document = merchantCategoryListItems.document();
		String id = document.getId();
		if (item.id == null) {
			item.id = id;
		}
		return document.set(item);
	}

	public Task<Void> pushMerchant(Merchant merchant) {
		String key = merchants.push().getKey();
		merchant.id = key;
		return merchants.child(key).setValue(merchant);
	}

	public ApiFuture<?> pushMerchantToStore(Merchant merchant) {
		DocumentReference document = storeMerchants.document();
		String id = document.getId();
		merchant.id = id;
		return document.set(merchant);
	}

	public void getMerchantsCategory(Merchant merchant, ValueEventListener listener) {
		getMerchantsCategory(merchant, ALCOHOL, listener);
	}

	public void getMerchantsCategory(Merchant merchant, ItemCategory category,
			ValueEventListener listener) {
		publicReadable.child(MERCHANT_CATEGORY_LIST_ITEMS).child(merchant.id + category)
				.addValueEventListener(listener);
	}

	public ApiFuture<QuerySnapshot> getItemsFromStore(ItemCategory itemCategory,
			Merchant merchant) {
		CollectionReference merchantCategoryListItems = store.collection(
				"merchantCategoryListItems");
		CollectionReference items = merchantCategoryListItems.document(merchant.id).collection(
				"items");
		return items.whereEqualTo("category", itemCategory).get();
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

	/**
	 * Create a dummy user (UI test uses this user)
	 *
	 * @return task for result of the operation
	 */
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
