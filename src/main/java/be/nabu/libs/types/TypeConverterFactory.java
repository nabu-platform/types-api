package be.nabu.libs.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.TypeConverter;

public class TypeConverterFactory {
	
	private static TypeConverterFactory instance;
	
	public static TypeConverterFactory getInstance() {
		if (instance == null)
			instance = new TypeConverterFactory();
		return instance;
	}
	
	private List<TypeConverter> converters = new ArrayList<TypeConverter>();
	
	@SuppressWarnings("unchecked")
	public TypeConverter getConverter() {
		if (converters.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				converters.addAll((List<TypeConverter>) declaredMethod.invoke(null, TypeConverter.class));
			}
			catch (ClassNotFoundException e) {
				// ignore, the framework is not present
			}
			catch (NoSuchMethodException e) {
				// corrupt framework?
				throw new RuntimeException(e);
			}
			catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				// ignore
			}
			catch (InvocationTargetException e) {
				// ignore
			}
			if (converters.isEmpty()) {
				ServiceLoader<TypeConverter> serviceLoader = ServiceLoader.load(TypeConverter.class);
				for (TypeConverter converter : serviceLoader) {
					converters.add(converter);
				}
			}
		}
		return new MultipleTypeConverter(converters);
	}
	
	public void addConverter(TypeConverter converter) {
		converters.add(converter);
	}
	
	public void removeConverter(TypeConverter converter) {
		converters.remove(converter);
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
}
