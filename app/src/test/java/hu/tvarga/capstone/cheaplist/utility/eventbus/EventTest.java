package hu.tvarga.capstone.cheaplist.utility.eventbus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {

	@Mock
	private EventBusWrapper eventBusWrapper;

	@Mock
	private EventBusBuffer eventBusBuffer;

	private Object object;
	private Event event;

	@Before
	public void setUp() throws Exception {
		object = new Object();
		event = new Event(eventBusBuffer, eventBusWrapper);
	}

	@Test
	public void post() throws Exception {
		event.post(object);

		verify(eventBusBuffer).buffer(object);
		verify(eventBusWrapper).post(object);
	}

	@Test
	public void register() throws Exception {
		event.register(object);

		verify(eventBusWrapper).register(object);
	}

	@Test
	public void unregister() throws Exception {
		event.unregister(object);

		verify(eventBusWrapper).unregister(object);
	}
}