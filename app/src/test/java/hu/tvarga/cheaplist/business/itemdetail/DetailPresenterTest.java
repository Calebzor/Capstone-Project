package hu.tvarga.cheaplist.business.itemdetail;

import com.google.firebase.firestore.ListenerRegistration;

import org.junit.Test;
import org.mockito.Mock;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListManager;
import hu.tvarga.cheaplist.dao.ManufacturerInformation;
import hu.tvarga.cheaplist.dao.Merchant;
import hu.tvarga.cheaplist.dao.ShoppingListItem;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DetailPresenterTest extends MockitoJUnitTest {

    @Mock
    private ShoppingListManager shoppingListManager;

    @Mock
    private DetailPresenter presenter;

    @Mock
    private ListenerRegistration shoppingListItemListenerRegistration;

    @Mock
    private ListenerRegistration itemRefListenerRegistration;

    @Mock
    private DetailContract.View view;

    @Mock
    private ShoppingListItem shoppingListItem;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        presenter = new DetailPresenter(shoppingListManager, firebaseAuth, firebaseFirestore);
    }

    @Test
    public void getManufacturerInformation() throws Exception {
        ManufacturerInformation manufacturerInformation = new ManufacturerInformation();
        manufacturerInformation.address = "address";
        manufacturerInformation.contact = "contact";
        manufacturerInformation.supplier = "supplier";

        assertThat(presenter.getManufacturerInformation(manufacturerInformation),
                containsString(manufacturerInformation.address));
        assertThat(presenter.getManufacturerInformation(manufacturerInformation),
                containsString(manufacturerInformation.contact));
        assertThat(presenter.getManufacturerInformation(manufacturerInformation),
                containsString(manufacturerInformation.supplier));
    }

    @Test
    public void getManufacturerInformation_emptyString() throws Exception {
        ManufacturerInformation manufacturerInformation = new ManufacturerInformation();

        assertThat(presenter.getManufacturerInformation(manufacturerInformation), is(""));
    }

    @Test
    public void onPause() throws Exception {
        presenter.shoppingListItemListenerRegistration = shoppingListItemListenerRegistration;
        presenter.itemRefListenerRegistration = itemRefListenerRegistration;

        presenter.onPause();

        assertNull(presenter.item);
        assertThat(presenter.view, instanceOf(DetailViewStub.class));
        verify(shoppingListItemListenerRegistration).remove();
        verify(itemRefListenerRegistration).remove();
    }

    @Test
    public void addToShoppingList() throws Exception {
        Merchant merchant = mock(Merchant.class);

        presenter.addToShoppingList(shoppingListItem, merchant);

        verify(shoppingListManager).addToList(shoppingListItem, merchant);
    }

    @Test
    public void removeItemFromShoppingList() throws Exception {
        presenter.item = shoppingListItem;

        presenter.removeItemFromShoppingList();

        verify(shoppingListManager).removeFromList(shoppingListItem);
    }

    @Test
    public void onResume_NoUser() throws Exception {
        presenter.onResume(view, shoppingListItem);

        assertEquals(view, presenter.view);
        assertEquals(presenter.item, shoppingListItem);
        verify(view).updateUI(shoppingListItem);
        verifyNoMoreInteractions(firebaseFirestore);
    }
}