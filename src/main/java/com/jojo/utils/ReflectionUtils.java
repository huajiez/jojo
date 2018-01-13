package com.jojo.utils;


import com.google.common.base.Strings;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * 
 * @author Jame.HU
 * @date 2013-5-8 ÉÏÎç10:24:45
 */
public class ReflectionUtils {
	// -------------------------- STATIC METHODS --------------------------

	public static <C> C createInstance(Class<C> clz) {
		try {
			return clz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Exception occurs while " + "instancing object through the type named, has "
					+ "not a no-argument constructor?" + clz.getName(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static PropertyDescriptor[] getClassPropertyDescriptors(final Class clz) {
		return getClassPropertyDescriptor(clz, Object.class);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getBeanPropertyNames(final Class clz) {
		PropertyDescriptor[] descriptors = getClassPropertyDescriptors(clz);

		String[] propertyNames = new String[descriptors.length];
		for (int i = 0, propertyNamesLength = propertyNames.length; i < propertyNamesLength; i++) {
			propertyNames[i] = descriptors[i].getName();
		}
		return propertyNames;
	}

	@SuppressWarnings("rawtypes")
	public static PropertyDescriptor[] getClassPropertyDescriptor(final Class clz, final Class stopClass) {
		try {
			return Introspector.getBeanInfo(clz, stopClass).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static PropertyDescriptor[] getClassPropertyDescriptors(final Class clz, final Class requireType) {
		PropertyDescriptor[] descriptors = getClassPropertyDescriptors(clz);
		List<PropertyDescriptor> results = new ArrayList<PropertyDescriptor>();

		for (PropertyDescriptor descriptor : descriptors) {
			if (requireType.isAssignableFrom(descriptor.getPropertyType())) {
				results.add(descriptor);
			}
		}

		return results.toArray(new PropertyDescriptor[results.size()]);
	}

	@SuppressWarnings("rawtypes")
	public static PropertyDescriptor[] getClassPropertyDescriptors(final Class clz, final String[] propertyNames) {
		return getClassPropertyDescriptors(clz, propertyNames, Object.class);
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, PropertyDescriptor> getClassPropertyDescriptorMap(final Class clz) {

		PropertyDescriptor[] descriptors = getClassPropertyDescriptors(clz);
		Map<String, PropertyDescriptor> result = new HashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor descriptor : descriptors) {
			result.put(descriptor.getName(), descriptor);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static PropertyDescriptor[] getClassPropertyDescriptors(final Class clz, final String[] propertyNames,
			final Class stopClass) {
		PropertyDescriptor[] descriptors = getClassPropertyDescriptor(clz, stopClass);
		List<PropertyDescriptor> result = new ArrayList<PropertyDescriptor>();

		for (String propertyName : propertyNames) {
			boolean found = false;
			for (PropertyDescriptor descriptor : descriptors) {
				if (descriptor.getName().equalsIgnoreCase(propertyName)) {
					result.add(descriptor);
					found = true;
					break;
				}
			}
			if (!found) {
				result.add(null);
			}
		}
		return result.toArray(new PropertyDescriptor[result.size()]);
	}

	@SuppressWarnings("rawtypes")
	public static Class getGenericClassArgumentType(final Class clz) {
		return getGenericClassArgumentType(clz, 0);
	}

	@SuppressWarnings("rawtypes")
	public static Class getGenericClassArgumentType(Class clz, final int argumentIndex) {
		Type genType = clz.getGenericSuperclass();
		while (!(genType instanceof ParameterizedType)) {
			if (clz == Object.class) {
				return Object.class;
			}
			clz = clz.getSuperclass();
			genType = clz.getGenericSuperclass();
		}

		Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();

		if (argumentIndex >= parameters.length || argumentIndex < 0) {
			throw new RuntimeException(
					"Index: " + argumentIndex + ", Size of Parameterized Type: " + parameters.length);
		}

		final Type type = parameters[argumentIndex];
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			String clzName = ((Class) genericArrayType.getGenericComponentType()).getName();

			return forNameImpl("[L" + clzName + ";");
		} else {
			throw new RuntimeException("Unexpected generic type parameter encounter:" + type.getClass());
		}
	}

	@SuppressWarnings("rawtypes")
	private static Class forNameImpl(final String clzName) {
		try {
			return Class.forName(clzName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Class forName(final String clzname) {
		try {
			return Class.forName(clzname);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Class getArrayClass(final Class clz, final int dimension) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dimension; i++) {
			sb.append("[");
		}
		sb.append("L");
		sb.append(clz.getName());
		sb.append(";");
		return forNameImpl(sb.toString());
	}

	public static void fromTo(final Object source, final Object target, final String... skipFieldNames) {
		if (source == null || target == null) {
			return;
		}

		try {
			Set<String> skipFieldNamesSet = new HashSet<String>(Arrays.asList(skipFieldNames));

			PropertyDescriptor[] sourceDescriptors = Introspector.getBeanInfo(source.getClass())
					.getPropertyDescriptors();

			PropertyDescriptor[] targetDescriptors = Introspector.getBeanInfo(target.getClass())
					.getPropertyDescriptors();

			for (PropertyDescriptor sourceDescriptor : sourceDescriptors) {
				String propertyName = sourceDescriptor.getName();
				PropertyDescriptor targetDescriptor = getTargetPropertyDescriptor(propertyName, targetDescriptors);

				if (!skipFieldNamesSet.contains(propertyName) && targetDescriptor != null) {
					Method readMethod = sourceDescriptor.getReadMethod();
					Method writeMethod = targetDescriptor.getWriteMethod();

					if (readMethod == null || writeMethod == null) {
						continue;
					}

					try {
						readMethod.setAccessible(true);
						writeMethod.setAccessible(true);
						final Object original = readMethod.invoke(source);
						final Class<?> propertyType = targetDescriptor.getPropertyType();

						// if already assignable, no need to do conversion.
						if (propertyType.isInstance(original)) {
							writeMethod.invoke(target, original);
							continue;
						}
					} catch (Exception e) {
						throw new RuntimeException("Error during populating property.", e);
					}
				}
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException("An error occur during bean populating.", e);
		}
	}

	private static PropertyDescriptor getTargetPropertyDescriptor(final String propertyName,
			final PropertyDescriptor[] targetDescriptors) {
		for (PropertyDescriptor targetDescriptor : targetDescriptors) {
			if (targetDescriptor.getName().equals(propertyName)) {
				return targetDescriptor;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(final Class<T> clz, final int length) {
		return (T[]) Array.newInstance(clz, length);
	}

	public static Object invoke(final Method readMethod, final Object object, final Object... args) {
		try {
			if (args.length == 0) {
				return readMethod.invoke(object);
			} else {
				return readMethod.invoke(object, new Object[] { args });
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object get(final Field field, final Object object) {
		try {
			field.setAccessible(true);
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> String toString(T object) {
		Map<String, Object> fields = new LinkedHashMap<String, Object>();

		Map<String, PropertyDescriptor> propertyDescriptors = null;
		try {
			propertyDescriptors = ReflectionUtils.getClassPropertyDescriptorMap(object.getClass());
		} catch (Exception ex) {
			// ignore exception ...
		}

		if (propertyDescriptors == null) {
			return "";
		}

		for (String key : propertyDescriptors.keySet()) {
			Method readMethod = null;
			PropertyDescriptor propertyDescriptor = propertyDescriptors.get(key);

			if (propertyDescriptor == null || (readMethod = propertyDescriptor.getReadMethod()) == null) {
				fields.put(key, null);
				continue;
			}

			boolean isAccessible = false;
			try {
				if (!(isAccessible = readMethod.isAccessible())) {
					readMethod.setAccessible(true);
				}

				Object propertyValue = readMethod.invoke(object);
				if (propertyDescriptor.getPropertyType().isArray()) {
					propertyValue = Arrays.asList((Object[]) propertyValue);
				}

				fields.put(key, propertyValue);
			} catch (Exception ex) {
				// Ignore exception...
			} finally {
				readMethod.setAccessible(isAccessible);
			}
		}

		return fields.toString();
	}

	public static Object getFieldValue(Object source, String fieldName) {
		if (source == null) {
			throw new IllegalArgumentException("The argument source must not be null!");
		}

		if (!Strings.isNullOrEmpty(fieldName)) {
			throw new IllegalArgumentException("The argument fieldName must not be null or empty!");
		}

		Object fieldValue = null;
		try {
			Field field = source.getClass().getDeclaredField(fieldName);
			if (field != null) {
				field.setAccessible(true);
				fieldValue = field.get(source);
			}
		} catch (Exception ex) {
			// Exception occurs, Can't get the specified field, will return null
		}

		return fieldValue;
	}

	public static Object getFieldValueByType(Object source, Class<?> fieldType) {
		if (source == null) {
			throw new IllegalArgumentException("The argument source must not be null!");
		}

		if (fieldType == null) {
			throw new IllegalArgumentException("The argument fieldType must not be null!");
		}

		Field[] fields = source.getClass().getDeclaredFields();
		Field foundField = null;
		for (Field field : fields) {
			if (fieldType.isAssignableFrom(field.getType())) {
				foundField = field;
				break;
			}
		}

		Object fieldValue = null;
		try {
			if (foundField != null) {
				foundField.setAccessible(true);
				fieldValue = foundField.get(source);
			}
		} catch (Exception ex) {
			// Exception occurs, Can't get the specified field, will return null
		}

		return fieldValue;
	}

	
}
