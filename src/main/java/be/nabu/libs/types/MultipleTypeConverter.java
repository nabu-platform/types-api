package be.nabu.libs.types;

import java.util.List;

import be.nabu.libs.types.api.TypeConverter;
import be.nabu.libs.types.api.TypeInstance;

public class MultipleTypeConverter implements TypeConverter {

	private List<TypeConverter> converters;
	
	public MultipleTypeConverter(List<TypeConverter> converters) {
		this.converters = converters;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object instance, TypeInstance from, TypeInstance to) {
		Object converted = null;
		for (TypeConverter converter : converters) {
			converted = converter.convert(instance, from, to);
			if (converted != null)
				break;
		}
		return (T) converted;
	}

	@Override
	public boolean canConvert(TypeInstance from, TypeInstance to) {
		for (TypeConverter converter : converters) {
			if (converter.canConvert(from, to))
				return true;
		}
		return false;
	}

}
