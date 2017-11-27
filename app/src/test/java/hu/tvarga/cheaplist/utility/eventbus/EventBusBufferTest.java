package hu.tvarga.cheaplist.utility.eventbus;

import org.junit.Test;
import org.mockito.Mock;

import hu.tvarga.cheaplist.MockitoJUnitTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventBusBufferTest extends MockitoJUnitTest {

	@Mock
	private Object object;

	@Mock
	private EventBusWrapper eventBusWrapper;

	private EventBusBuffer eventBusBuffer;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		eventBusBuffer = new EventBusBuffer(eventBusWrapper);
	}

	@Test
	public void bufferingIfStartBuffering() throws Exception {
		eventBusBuffer.startBuffering();
		eventBusBuffer.buffer(object);

		assertEquals(object, eventBusBuffer.getBuffer().peek());
	}

	@Test
	public void notBufferingIfNotStartedBuffering() throws Exception {
		eventBusBuffer.buffer(object);

		assertTrue("Buffer should not add stuff if it is not buffering",
				eventBusBuffer.getBuffer().isEmpty());
	}

	@Test
	public void notBufferingAfterReplayAll() throws Exception {
		eventBusBuffer.startBuffering();
		eventBusBuffer.replayAndClearBuffer();

		assertFalse("eventBusBuffer should stop buffering after replaying objects",
				eventBusBuffer.isBuffering());
	}

	@Test
	public void replaySingleObject() throws Exception {
		eventBusBuffer.startBuffering();
		eventBusBuffer.buffer(object);

		eventBusBuffer.replayAndClearBuffer();

		// The same buffered object should have been replayed...
		verify(eventBusWrapper, times(1)).post(eq(object));

		assertTrue("After replaying buffer should be empty", eventBusBuffer.getBuffer().isEmpty());
	}

	@Test
	public void replayMultipleObjects() throws Exception {
		eventBusBuffer.startBuffering();
		eventBusBuffer.buffer(object);
		eventBusBuffer.buffer(object);
		eventBusBuffer.buffer(object);

		eventBusBuffer.replayAndClearBuffer();

		// All buffered objects should have been replayed
		verify(eventBusWrapper, times(3)).post(any(Object.class));
	}
}