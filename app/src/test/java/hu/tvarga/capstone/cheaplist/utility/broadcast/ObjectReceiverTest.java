package hu.tvarga.capstone.cheaplist.utility.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.junit.Test;
import org.mockito.Mock;

import java.io.Serializable;

import hu.tvarga.capstone.cheaplist.BaseMockitoJUnitTest;

import static hu.tvarga.capstone.cheaplist.utility.broadcast.Broadcast.CANCELLED;
import static hu.tvarga.capstone.cheaplist.utility.broadcast.Broadcast.ERROR;
import static hu.tvarga.capstone.cheaplist.utility.broadcast.Broadcast.RESULT;
import static hu.tvarga.capstone.cheaplist.utility.broadcast.Broadcast.RESULT_TYPE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ObjectReceiverTest extends BaseMockitoJUnitTest {

	@Mock
	private LocalBroadcastManagerWrapper localBroadcastManagerWrapper;

	@Mock
	private ObjectListener<TestClass1> listener;

	@Mock
	private Context context;

	@Mock
	private Intent intent;

	@Mock
	private IntentFilter intentFilter;

	/**
	 * We use the factory here for free test coverage on that class
	 */
	private ObjectReceiverFactory factory;

	private ObjectReceiver<TestClass1> receiver;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		doReturn(intentFilter).when(localBroadcastManagerWrapper).getNewIntentFilter();

		factory = new ObjectReceiverFactory(localBroadcastManagerWrapper);

		receiver = factory.get(listener, TestClass1.class);
	}

	// region object creation
	@Test
	public void createObjectWithClass() {
		verify(intentFilter).addAction(eq(TestClass1.class.toString()));

		receiver.register();

		verify(localBroadcastManagerWrapper).registerReceiver(eq(receiver), eq(intentFilter));

		receiver.onReceive(context, intent);

		verify(localBroadcastManagerWrapper, never()).unregisterReceiver(eq(receiver));
	}

	@Test
	public void createObjectWithClass_unregisterAfterReceivingBroadcast() {

		// Setup our custom broadcast receiver to verify the constructor with the extra boolean
		ObjectReceiver<TestClass1> receiver = factory.get(listener, TestClass1.class, true);

		receiver.register();

		verify(localBroadcastManagerWrapper).registerReceiver(eq(receiver), eq(intentFilter));

		receiver.onReceive(context, intent);

		verify(localBroadcastManagerWrapper).unregisterReceiver(eq(receiver));
	}
	// endregion

	// region broadcast results
	@Test
	public void handleBroadcastSuccess() {
		TestClass1 testObject = new TestClass1();

		when(intent.getSerializableExtra(eq(RESULT))).thenReturn(testObject);

		receiver.onReceive(context, intent);

		verify(listener).onReceive(eq(testObject));
	}

	@Test
	public void handleBroadcastSuccess_nullObject() {
		when(intent.getSerializableExtra(eq(RESULT))).thenReturn(null);

		receiver.onReceive(context, intent);

		verify(listener).onFailure(nullable(Throwable.class));
	}

	@Test
	public void handleBroadcastSuccess_wrongObject() {
		TestClass2 wrongObject = mock(TestClass2.class);

		when(intent.getSerializableExtra(eq(RESULT))).thenReturn(wrongObject);

		receiver.onReceive(context, intent);

		verify(listener).onFailure(nullable(Throwable.class));
	}

	@Test
	public void handleBroadcastFailure() {
		Throwable throwable = mock(Throwable.class);
		when(intent.getSerializableExtra(eq(RESULT))).thenReturn(throwable);
		when(intent.getSerializableExtra(eq(RESULT_TYPE))).thenReturn(ERROR);

		receiver.onReceive(context, intent);

		verify(listener).onFailure(eq(throwable));
	}

	@Test
	public void handleBroadcastCancelled() {
		when(intent.getSerializableExtra(eq(RESULT_TYPE))).thenReturn(CANCELLED);

		receiver.onReceive(context, intent);

		verify(listener).onCancelled();
	}
	// endregion

	// region lifecycle handling
	@Test
	public void register() {
		reset(localBroadcastManagerWrapper);

		receiver.register();
		verify(localBroadcastManagerWrapper, times(1)).registerReceiver(eq(receiver),
				eq(intentFilter));

		// It is not possible to register twice
		receiver.register();
		verify(localBroadcastManagerWrapper, times(1)).registerReceiver(eq(receiver),
				eq(intentFilter));

		receiver.unregister();
		receiver.register();
		verify(localBroadcastManagerWrapper, times(2)).registerReceiver(eq(receiver),
				eq(intentFilter));
	}

	@Test
	public void unregister() {
		receiver.register();
		receiver.unregister();

		// It is not possible to unregister same receiver twice
		receiver.unregister();
		verify(localBroadcastManagerWrapper, times(1)).unregisterReceiver(eq(receiver));
	}
	// endregion

	// helper test classes
	private class TestClass1 implements Serializable {

		private static final long serialVersionUID = -6879063976156132101L;

	}

	@SuppressWarnings("unused")
	private class TestClass2<T> implements Serializable {

		private static final long serialVersionUID = 2820026436694663508L;

	}
	// endregion

}

