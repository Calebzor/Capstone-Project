package hu.tvarga.capstone.cheaplist.firebasetest;

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

import hu.tvarga.capstone.cheaplist.dao.Item;
import hu.tvarga.capstone.cheaplist.dao.Merchant;
import hu.tvarga.capstone.cheaplist.dao.MerchantCategoryListItem;

import static com.jayway.awaitility.Awaitility.await;
import static hu.tvarga.capstone.cheaplist.firebasetest.TestDBEntries.getSoproni;
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
		Task<UserRecord> task = firebaseHelper.createUser();

		assertTaskSuccess(task);
	}

	@Ignore
	@Test
	public void pushCategories() {
		Task<Void> task = firebaseHelper.pushItemCategories();

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

	private Merchant getMerchant1() {
		Merchant merchant = new Merchant();
		merchant.id = "-KtftTTSOMgtW0zXyGtk";
		return merchant;
	}

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
		await().atMost(Duration.TWO_MINUTES).until(() -> items.size() == 2);
	}

	private static void assertTaskSuccess(Task<?> hello) {
		final Boolean[] callSucceeded = {false};
		hello.addOnSuccessListener(result -> callSucceeded[0] = true);
		await().until(() -> callSucceeded[0]);
	}

}