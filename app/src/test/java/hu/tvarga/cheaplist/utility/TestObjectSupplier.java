package hu.tvarga.cheaplist.utility;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestObjectSupplier {

	private static ImmutableMap<Class<?>, Supplier<?>> DEFAULT_MAPPERS;

	static {
		final ImmutableMap.Builder<Class<?>, Supplier<?>> mapperBuilder = ImmutableMap.builder();

		//region basic types
		mapperBuilder.put(short.class, new Supplier<Short>() {
			@Override
			public Short get() {
				return 0;
			}
		});
		mapperBuilder.put(Short.class, new Supplier<Short>() {
			@Override
			public Short get() {
				return 0;
			}
		});
		mapperBuilder.put(int.class, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return 0;
			}
		});
		mapperBuilder.put(Integer.class, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return 0;
			}
		});
		mapperBuilder.put(double.class, new Supplier<Double>() {
			@Override
			public Double get() {
				return 1D;
			}
		});
		mapperBuilder.put(Double.class, new Supplier<Double>() {
			@Override
			public Double get() {
				return 1D;
			}
		});
		mapperBuilder.put(float.class, new Supplier<Float>() {
			@Override
			public Float get() {
				return 1F;
			}
		});
		mapperBuilder.put(Float.class, new Supplier<Float>() {
			@Override
			public Float get() {
				return 1F;
			}
		});
		mapperBuilder.put(long.class, new Supplier<Long>() {
			@Override
			public Long get() {
				return 1L;
			}
		});
		mapperBuilder.put(Long.class, new Supplier<Long>() {
			@Override
			public Long get() {
				return 1L;
			}
		});
		mapperBuilder.put(boolean.class, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return false;
			}
		});
		mapperBuilder.put(Boolean.class, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return false;
			}
		});
		mapperBuilder.put(byte.class, new Supplier<Byte>() {
			@Override
			public Byte get() {
				return Byte.valueOf("a");
			}
		});
		mapperBuilder.put(Byte.class, new Supplier<Byte>() {
			@Override
			public Byte get() {
				return Byte.valueOf("a");
			}
		});
		mapperBuilder.put(char.class, new Supplier<Character>() {
			@Override
			public Character get() {
				return 'c';
			}
		});
		mapperBuilder.put(Character.class, new Supplier<Character>() {
			@Override
			public Character get() {
				return 'c';
			}
		});

		mapperBuilder.put(BigDecimal.class, new Supplier<BigDecimal>() {
			@Override
			public BigDecimal get() {
				return new BigDecimal(0);
			}
		});
		mapperBuilder.put(Date.class, new Supplier<Date>() {
			@Override
			public Date get() {
				return new Date();
			}
		});
		mapperBuilder.put(Calendar.class, new Supplier<Calendar>() {
			@Override
			public Calendar get() {
				return Calendar.getInstance();
			}
		});
		mapperBuilder.put(JsonElement.class, new Supplier<JsonElement>() {
			@Override
			public JsonElement get() {
				return new JsonObject();
			}
		});
		//endregion

		//region collections

		mapperBuilder.put(Set.class, new Supplier<Set>() {
			@Override
			public Set get() {
				return Collections.EMPTY_SET;
			}
		});
		mapperBuilder.put(List.class, new Supplier<List>() {
			@Override
			public List get() {
				return Collections.EMPTY_LIST;
			}
		});
		mapperBuilder.put(Map.class, new Supplier<Map>() {
			@Override
			public Map get() {
				return Collections.EMPTY_MAP;
			}
		});
		//endregion

		//region our own common types

		mapperBuilder.put(UUID.class, new Supplier<UUID>() {
			@Override
			public UUID get() {
				return UUID.randomUUID();
			}
		});
		mapperBuilder.put(Integer[].class, new Supplier<Integer[]>() {
			@Override
			public Integer[] get() {
				return new Integer[0];
			}
		});
		mapperBuilder.put(String[].class, new Supplier<String[]>() {
			@Override
			public String[] get() {
				return new String[0];
			}
		});
		mapperBuilder.put(byte[].class, new Supplier<byte[]>() {
			@Override
			public byte[] get() {
				return new byte[0];
			}
		});
		//endregion

		DEFAULT_MAPPERS = mapperBuilder.build();
	}

	protected Set<String> ignored;

	private ImmutableMap<Class<?>, Supplier<?>> mappers;

	/**
	 * This is the entry point for providing your own mappers the way you do that is:
	 * <p>
	 * <p>
	 * {@code // Create your custom mapper private static final Map<Class<?>, Supplier<?>>
	 * customMappers = new HashMap<>(); static { customMappers.put(YourCustomClassInTheSUT.class,
	 * new Supplier<UUID>() {
	 *
	 * @param customMappers Any custom mappers for a given class type.
	 * @param ignored       The methods which should be ignored (e.g., "getId" or "isActive").
	 * @Override public YourCustomClassInTheDto get() { return YourCustomClassInTheSUT
	 * .getInstance(); } }); }
	 * <p>
	 * // Your test has to extend TestObjectSupplier<YourSUT> public YourSUTTest() { super
	 * (customMappers, null); }
	 * <p>
	 * // Provide an instance by overriding the getInstance
	 * @Override protected OrderDetail getInstance() { return new YourDto(); } }
	 * <p>
	 * ---------------------------------------------------------------------------------------------
	 * <p>
	 * Creates an instance of {@link TestObjectSupplier} with ignore fields and additional custom
	 * mappers.
	 */
	TestObjectSupplier(Map<Class<?>, Supplier<?>> customMappers, Set<String> ignored) {
		this.ignored = new HashSet<>();
		if (ignored != null) {
			this.ignored.addAll(ignored);
		}
		this.ignored.add("getClass");

		if (customMappers == null) {
			this.mappers = DEFAULT_MAPPERS;
		}
		else {
			final ImmutableMap.Builder<Class<?>, Supplier<?>> builder = ImmutableMap.builder();
			builder.putAll(customMappers);
			builder.putAll(DEFAULT_MAPPERS);
			this.mappers = builder.build();
		}
	}

	/**
	 * Creates an object for the given {@link Class}.
	 *
	 * @param fieldName The name of the field.
	 * @param clazz     The {@link Class} type to create.
	 * @return A new instance for the given {@link Class}.
	 * @throws InstantiationException If this Class represents an abstract class, an interface, an
	 *                                array class, a primitive type, or void; or if the class has no
	 *                                default constructor; or if the instantiation fails for some
	 *                                other reason.
	 * @throws IllegalAccessException If the class or its default constructor is not accessible.
	 */
	Object createObject(String fieldName, Class<?> clazz)
			throws InstantiationException, IllegalAccessException {

		try {
			final Supplier<?> supplier = this.mappers.get(clazz);
			if (supplier != null) {
				return supplier.get();
			}

			if (clazz.isEnum()) {
				return clazz.getEnumConstants()[0];
			}

			return clazz.newInstance();
		}
		catch (IllegalAccessException e) {
			throw new StubObjectCreationException(
					"IllegalAccessException. Unable to create objects for field '" + fieldName +
							"'.", e);
		}
		catch (InstantiationException e) {
			throw new StubObjectCreationException(
					"InstantiationException. Unable to create objects for field '" + fieldName +
							"'.", e);
		}
	}

	public class StubObjectCreationException extends RuntimeException {

		private static final long serialVersionUID = 7829950792807987428L;

		StubObjectCreationException(String message, Exception source) {
			super(message, source);
		}
	}
}

