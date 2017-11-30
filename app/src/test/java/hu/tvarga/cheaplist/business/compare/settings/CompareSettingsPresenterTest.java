package hu.tvarga.cheaplist.business.compare.settings;

import org.junit.Test;
import org.mockito.Mock;

import hu.tvarga.cheaplist.MockitoJUnitTest;
import hu.tvarga.cheaplist.business.compare.CompareService;
import hu.tvarga.cheaplist.business.user.UserService;
import hu.tvarga.cheaplist.utility.eventbus.Event;

import static org.mockito.Mockito.verify;

public class CompareSettingsPresenterTest extends MockitoJUnitTest {

	@Mock
	private CompareService compareService;

	@Mock
	private UserService userService;

	@Mock
	private Event event;

	private CompareSettingsPresenter presenter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		presenter = new CompareSettingsPresenter(compareService, userService, event);
	}

	@Test
	public void onStart() throws Exception {
		presenter.onStart();

		verify(event).register(presenter);
	}

	@Test
	public void onStop() throws Exception {
		presenter.onStop();

		verify(event).unregister(presenter);
	}
}