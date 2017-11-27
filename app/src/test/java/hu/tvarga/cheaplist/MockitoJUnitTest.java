package hu.tvarga.cheaplist;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.support.annotation.CallSuper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import hu.tvarga.cheaplist.utility.StringUtils;
import hu.tvarga.cheaplist.utility.eventbus.Event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class MockitoJUnitTest extends Assert {

	private static final String STRING_PLACEHOLDER = "Placeholder #%1$s: %2$s";

	@Mock
	protected ContextWrapper contextWrapper;

	@Mock
	protected Event event;

	@SuppressLint("UseSparseArrays")
	private Map<Integer, String> customStrings = new HashMap<>();

	@Before
	@CallSuper
	public void setUp() throws Exception {
		setupContextWrapperResourceHandling();
	}

	// not calling this verify because auto complete is then annoying
	protected void checkEventRegister() {
		verify(event).register(any());
	}

	// not calling this verify because auto complete is then annoying
	protected void checkEventUnregister() {
		verify(event).unregister(any());
	}

	/**
	 * This method makes sure that the contextWrapper will return values based on the resID.
	 * This is a swift way of determining whether the correct resource has been returned by the
	 * contextWrapper as the resID is always unique
	 */
	private void setupContextWrapperResourceHandling() {
		doAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Integer resId = invocation.getArgument(0);
				return customStrings.containsKey(resId) ? customStrings.get(resId) : String.valueOf(
						resId);
			}
		}).when(contextWrapper).getString(anyInt());

		// We need to call it once to avoid unnecessary stubbing errors
		contextWrapper.getString(0);

		doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArgument(0);
			}
		}).when(contextWrapper).getColor(anyInt());

		// We need to call it once to avoid unnecessary stubbing errors
		contextWrapper.getColor(0);
	}

	/**
	 * This is a helper function that can return Strings with placeholders according to what you
	 * want to test. It automatically adds this to the customStrings map so
	 * the contextWrapper will return this String with placeholders for the given resId
	 *
	 * @param resId        The resId you know should have placeholders
	 * @param textToInsert The texts you want to insert into the placeholder
	 * @return The resId string with text already inside the placeholder
	 */
	protected String setupPlaceholder(int resId, Object... textToInsert) {
		String stringWithPlaceholders = String.valueOf(resId);
		for (int i = 1; i <= textToInsert.length; i++) {
			stringWithPlaceholders += ", ";
			String placeholderCounter = "%" + i + "$s";
			stringWithPlaceholders += StringUtils.setTextToPlaceholder(STRING_PLACEHOLDER, i,
					placeholderCounter);
		}
		addCustomStringResource(resId, stringWithPlaceholders);
		return StringUtils.setTextToPlaceholder(stringWithPlaceholders, textToInsert);
	}

	/**
	 * If you really want to, you can add your own custom String to be returned for a given resId
	 * from the ContextWrapper. Mainly used to generate Strings with placeholders
	 *
	 * @param resId  The resId you want to return a custom string
	 * @param string The custom string you want returned for the resId
	 */
	protected void addCustomStringResource(int resId, String string) {
		customStrings.put(resId, string);
	}

	/**
	 * This is a simple helper method so you can directly eqString(R.string.example) inside of
	 * verification methods
	 *
	 * @param resId The resId you want to compare
	 * @return an eq() object that passes a String object
	 */
	protected String eqString(int resId) {
		return eq(String.valueOf(resId));
	}

	/**
	 * This is a simple helper method so you can directly eqColor(R.color.example) inside of
	 * verification methods. eq(R.color.example) will also work, but this reads nicer
	 *
	 * @param resId Tje resId you want to compare
	 * @return an eq() object that passes an int object
	 */
	protected int eqColor(int resId) {
		return eq(resId);
	}
}
