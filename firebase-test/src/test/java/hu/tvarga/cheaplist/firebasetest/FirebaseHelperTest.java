package hu.tvarga.cheaplist.firebasetest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;
import com.jayway.awaitility.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import hu.tvarga.cheaplist.dao.Item;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.MerchantCategoryListItem;

import static com.jayway.awaitility.Awaitility.await;
import static hu.tvarga.cheaplist.firebasetest.FirebaseHelper.L;
import static hu.tvarga.cheaplist.firebasetest.TestDBEntries.getSoproni;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FirebaseHelperTest {

	private FirebaseHelper firebaseHelper;

	@Before
	public void setUp() {
		firebaseHelper = new FirebaseHelper();
		try {
			firebaseHelper.initializeFirebaseApp();
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
		//		firebaseHelper.detachDatabaseReadListener();
	}

	@Ignore
	@Test
	public void createUser() throws Exception {
		ApiFuture<UserRecord> task = firebaseHelper.createUser();

		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void deleteUser() throws Exception {
		boolean deleted = firebaseHelper.deleteUser();
		await().until(() -> deleted);
	}

	@Ignore
	@Test
	public void deleteAndCreateDummyUser() throws Exception {
		boolean deleted = firebaseHelper.deleteUser();
		await().until(() -> deleted);
		ApiFuture<UserRecord> task = firebaseHelper.createUser();
		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void pushCategories() {
		ApiFuture<?> task = firebaseHelper.pushItemCategories();

		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void pushToMerchants() {
		Merchant merchant = new Merchant();
		merchant.name = "TESCO";
		Task<Void> task = firebaseHelper.pushMerchant(merchant);

		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void pushToMerchantsToStore() {
		Merchant merchant = new Merchant();
		merchant.name = "TESCO";
		ApiFuture<?> documentReferenceApiFuture = firebaseHelper.pushMerchantToStore(merchant);

		assertTaskSuccess(documentReferenceApiFuture);
	}

	@Ignore
	@Test
	public void getItemsFromStore() throws Exception {
		Merchant merchant = getMerchant1();
		ApiFuture<QuerySnapshot> itemsFromStore = firebaseHelper.getItemsFromStore(
				ItemCategory.MEAT, merchant);

		List<DocumentSnapshot> documents = itemsFromStore.get().getDocuments();
		for (DocumentSnapshot document : documents) {
			L.debug(document.toObject(MerchantCategoryListItem.class));
		}
		assertEquals(2, documents.size());

	}

	@Ignore
	@Test
	public void dropTables() {
		firebaseHelper.dropTables();
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void pushToItems() {
		Item item = getSoproni();

		Merchant merchant = getMerchant1();

		Task<Void> task = firebaseHelper.pushItem(item, merchant);

		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void pushToItemsToStore() {
		Item item = getSoproni();

		Merchant merchant = getMerchant1();

		ApiFuture<?> apiFuture = firebaseHelper.pushItemToStore(item, merchant);

		assertTaskSuccess(apiFuture);
	}

	private Merchant getMerchant1() {
		Merchant merchant = new Merchant();
		merchant.id = "ws2QwmZ6UhsuwlvwlAz3";
		return merchant;
	}

	@Ignore
	@Test
	public void getMerchantsAlcohols() {
		final List<MerchantCategoryListItem> items = new ArrayList<>();
		firebaseHelper.getMerchantsCategory(getMerchant1(), new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for (DataSnapshot child : snapshot.getChildren()) {
					MerchantCategoryListItem item = child.getValue(MerchantCategoryListItem.class);
					items.add(item);
				}
			}

			@Override
			public void onCancelled(DatabaseError error) {

			}
		});
		await().atMost(Duration.TWO_MINUTES).until(() -> items.size() != 0);
	}

	private static void assertTaskSuccess(Task<?> hello) {
		final Boolean[] callSucceeded = {false};
		hello.addOnSuccessListener(result -> callSucceeded[0] = true);
		await().until(() -> callSucceeded[0]);
	}

	private void assertTaskSuccess(ApiFuture<?> documentReferenceApiFuture) {
		final Boolean[] callSucceeded = {false};
		documentReferenceApiFuture.addListener(() -> {
			callSucceeded[0] = true;
		}, Executors.newSingleThreadExecutor());
		await().until(() -> callSucceeded[0]);
	}

}