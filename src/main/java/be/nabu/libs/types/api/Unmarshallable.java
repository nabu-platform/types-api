package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

public interface Unmarshallable<T> extends SimpleType<T> {
	public T unmarshal(String content, Value<?>... values);
}
