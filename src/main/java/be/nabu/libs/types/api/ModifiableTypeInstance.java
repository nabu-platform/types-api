package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

public interface ModifiableTypeInstance extends TypeInstance {
	public void setType(Type type);
	public void setProperty(Value<?>...values);
}
