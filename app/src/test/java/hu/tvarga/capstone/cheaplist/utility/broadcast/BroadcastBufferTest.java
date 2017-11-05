package hu.tvarga.capstone.cheaplist.utility.broadcast;

import android.content.Intent;

import org.junit.Test;
import org.mockito.Mock;

import hu.tvarga.capstone.cheaplist.BaseMockitoJUnitTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BroadcastBufferTest extends BaseMockitoJUnitTest {

	@Mock
	private LocalBroadcastManagerWrapper localBroadcastManagerWrapper;

	@Mock
	private Intent intent;

	private BroadcastBuffer broadcastBuffer;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		broadcastBuffer = new BroadcastBuffer(localBroadcastManagerWrapper);
	}

	@Test
	public void testBufferingIfStartBuffering() throws Exception {

		broadcastBuffer.startBuffering();
		broadcastBuffer.buffer(intent);

		assertEquals(intent, broadcastBuffer.getBufferedBroadcasts().peek());
	}

	@Test
	public void testNotBufferingIfNotStartedBuffering() throws Exception {

		broadcastBuffer.buffer(intent);

		assertTrue("Buffer should not add stuff if it is not buffering",
				broadcastBuffer.getBufferedBroadcasts().isEmpty());
	}

	@Test
	public void testNotBufferingAfterReplayAll() throws Exception {
		broadcastBuffer.startBuffering();
		broadcastBuffer.replayAllAndClearBuffer();

		assertFalse("BroadcastBuffer should stop buffering after replaying intents",
				broadcastBuffer.isBuffering());
	}

	@Test
	public void testReplaySingleIntent() throws Exception {

		broadcastBuffer.startBuffering();
		broadcastBuffer.buffer(intent);

		broadcastBuffer.replayAllAndClearBuffer();

		// The same buffered intent should have been replayed...
		verify(localBroadcastManagerWrapper, times(1)).sendBroadcast(eq(intent));

		assertTrue("After replaying buffer should be empty",
				broadcastBuffer.getBufferedBroadcasts().isEmpty());
	}

	@Test
	public void testReplayMultipleIntents() throws Exception {

		broadcastBuffer.startBuffering();
		broadcastBuffer.buffer(intent);
		broadcastBuffer.buffer(intent);
		broadcastBuffer.buffer(intent);

		broadcastBuffer.replayAllAndClearBuffer();

		// All buffered intents should have been replayed
		verify(localBroadcastManagerWrapper, times(3)).sendBroadcast(any(Intent.class));
	}
}
