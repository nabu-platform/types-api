package be.nabu.libs.types;

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
	
	public TypeConverter getConverter() {
		if (converters.isEmpty()) {
			ServiceLoader<TypeConverter> serviceLoader = ServiceLoader.load(TypeConverter.class);
			for (TypeConverter converter : serviceLoader)
				converters.add(converter);
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
