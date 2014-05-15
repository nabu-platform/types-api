package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public interface TypeInstance {
	public Type getType();
	public <S> Value<S> getProperty(Property<S> property);
	public Value<?> [] getProperties();
}
