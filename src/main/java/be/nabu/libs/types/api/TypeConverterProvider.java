package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

/**
 * Allows you to convert an object instance from one type to another
 * The parameters may be necessary because they contain information about the type
 */
public interface TypeConverterProvider {
	
	public <T> T convert(Object instance, Value<?> [] sourceParameters, Value<?>...targetParameters);
	
	public Type getSourceType();
	public Type getTargetType();
	
}
