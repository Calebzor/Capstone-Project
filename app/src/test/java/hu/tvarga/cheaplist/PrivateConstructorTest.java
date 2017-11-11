package hu.tvarga.cheaplist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import hu.tvarga.cheaplist.utility.GsonHelper;
import hu.tvarga.cheaplist.utility.Preferences;
import hu.tvarga.cheaplist.utility.StringUtils;

import static java.lang.reflect.Modifier.isPrivate;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PrivateConstructorTest {

	private Class clazz;

	public PrivateConstructorTest(Class clazz) {
		this.clazz = clazz;
	}

	@Parameterized.Parameters(name = "{index}: private constructor test for: {0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(
				new Object[][]{{GsonHelper.class}, {Preferences.class}, {StringUtils.class},});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void constructorIsPrivate()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			InstantiationException {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		assertTrue("Private constructor expected for: " + constructor.getName(),
				isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}
}
