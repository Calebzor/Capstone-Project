package hu.tvarga.capstone.cheaplist.utility.eventbus;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventBusWrapperTest {

	@Mock
	private EventBus eventBus;

	@Mock
	private Object object;

	private EventBusWrapper eventBusWrapper;

	@Before
	public void setUp() throws Exception {
		eventBusWrapper = spy(new EventBusWrapper());
		when(eventBusWrapper.getDefault()).thenReturn(eventBus);
	}

	@Test
	public void post() throws Exception {
		eventBusWrapper.post(object);

		verify(eventBus).post(object);
	}

	@Test
	public void register() throws Exception {
		eventBusWrapper.register(object);

		verify(eventBus).register(object);
	}

	@Test
	public void unregister() throws Exception {
		eventBusWrapper.unregister(object);

		verify(eventBus).unregister(object);
	}
}