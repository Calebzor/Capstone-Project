package hu.tvarga.capstone.cheaplist.utility;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EventBusWrapperTest {

	@Test
	public void getDefault() throws Exception {
		EventBusWrapper eventBusWrapper = new EventBusWrapper();
		assertNotNull(eventBusWrapper.getDefault());
	}
}