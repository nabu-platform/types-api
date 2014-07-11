package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

public interface ModifiableType extends Type {
	public void setProperty(Value<?>...values);
}
