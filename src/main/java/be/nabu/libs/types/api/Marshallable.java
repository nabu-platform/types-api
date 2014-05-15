package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

public interface Marshallable<T> extends SimpleType<T> {
	public String marshal(T object, Value<?>... values);
}
