package hu.tvarga.cheaplist.utility;

import android.app.Dialog;
import android.graphics.Bitmap;

import com.google.common.base.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.tvarga.cheaplist.business.compare.CompareTabsViewStub;
import hu.tvarga.cheaplist.business.compare.shoppinglist.ShoppingListBaseViewStub;
import hu.tvarga.cheaplist.business.itemdetail.DetailViewStub;

import static org.junit.Assert.fail;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Helper class to cover view stubs. It verifies that the methods implemented by the view stub
 * call the log method with the method as the message
 */
@RunWith(Parameterized.class)
public class ViewStubTest extends TestObjectSupplier {

	private static final List<Object> SUT_CLASSES = Arrays.asList(
			new Object[]{ShoppingListBaseViewStub.class, CompareTabsViewStub.class,
					DetailViewStub.class});

	private static final Map<Class<?>, Supplier<?>> CUSTOM_VIEW_STUB_MAPPERS = new HashMap<>();

	private static final Set<String> IGNORED_METHODS = new HashSet<>();

	//	Define mappers that map to null here
	static {
		CUSTOM_VIEW_STUB_MAPPERS.put(Bitmap.class, new Supplier<Bitmap>() {
			@Override
			public Bitmap get() {
				return null;
			}
		});
		CUSTOM_VIEW_STUB_MAPPERS.put(Dialog.class, new Supplier<Dialog>() {
			@Override
			public Dialog get() {
				return null;
			}
		});

		IGNORED_METHODS.add("log");
		IGNORED_METHODS.add("$jacocoInit");
	}

	private Object stub;

	private Method[] declaredMethods;

	private Object stubSpy;

	private List<String> invocationReport;

	/**
	 * Creates an instance of {@link ViewStubTest} with the default ignore methods.
	 */
	public ViewStubTest(Class<?> clazz) throws IllegalAccessException, InstantiationException {
		super(CUSTOM_VIEW_STUB_MAPPERS, IGNORED_METHODS);
		stub = clazz.newInstance();
	}

	@Parameterized.Parameters(name = "{index}: ViewStubTest stub class: {0}")
	public static Iterable<Object> data() {
		return SUT_CLASSES;
	}

	@Test
	public void stubTest() {
		// Keep track of method invocations
		invocationReport = new ArrayList<>();
		InvocationListener invocationListener = new InvocationListener() {
			@Override
			public void reportInvocation(MethodInvocationReport methodInvocationReport) {
				String toString = methodInvocationReport.getInvocation().toString();
				invocationReport.add(toString);
			}
		};
		// Create a spy with our invocation listener to keep track of method invocations
		Class<?> aClass = stub.getClass();
		declaredMethods = aClass.getDeclaredMethods();
		stubSpy = mock(aClass, withSettings().spiedInstance(stub).defaultAnswer(CALLS_REAL_METHODS)
				.invocationListeners(invocationListener));

		invokeClassMethods();

		verifyLogIsCalledWithMethodNameAsMessage();
	}

	private void verifyLogIsCalledWithMethodNameAsMessage() {

		for (Method method : declaredMethods) {
			String methodName = method.getName();
			if (isIgnored(methodName)) {
				continue;
			}

			boolean foundMethodNameInLogInvocation = false;
			for (String invocation : invocationReport) {
				if (invocation.toLowerCase().contains(methodName.toLowerCase()) &&
						invocation.contains(".log(")) {
					foundMethodNameInLogInvocation = true;
					break;
				}
			}
			if (!foundMethodNameInLogInvocation) {
				fail("Dose not look like the method: " + methodName + " is calling log with the " +
						"method name as message");
			}
		}
	}

	private void invokeClassMethods() {
		for (Method method : declaredMethods) {
			String methodName = method.getName();
			if (isIgnored(methodName)) {
				continue;
			}
			Class<?>[] parameterTypes = method.getParameterTypes();
			try {
				if (parameterTypes.length == 0) {
					method.invoke(stubSpy);
				}
				else {
					Object[] arguments = new Object[parameterTypes.length];
					for (int i = 0; i < parameterTypes.length; i++) {
						try {
							arguments[i] = createObject(methodName, parameterTypes[i]);
						}
						catch (InstantiationException e) {
							fail("Failed to create parameter for method invocation, method: " +
									methodName + " parameterType: " + parameterTypes[i].getName() +
									" you might want to add a supplier for this in ViewStubTest" +
									".CUSTOM_VIEW_STUB_MAPPERS Exception: " + e.getMessage());
						}
						catch (Exception e) {
							// default to null
							arguments[i] = null;
						}
					}
					method.invoke(stubSpy, arguments);
				}
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				fail("Exception happened while invoking method: " + methodName + " exception " +
						"message: " + e.getMessage());
			}
		}
	}

	private boolean isIgnored(String methodName) {
		return ignored.contains(methodName);
	}

}
