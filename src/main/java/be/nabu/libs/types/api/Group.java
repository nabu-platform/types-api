package be.nabu.libs.types.api;

import java.util.Set;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.validator.api.Validator;
import be.nabu.libs.types.api.ComplexContent;

public interface Group extends Iterable<Element<?>> {
	public Value<?> [] getProperties();
	public void setProperty(Value<?>...values);
	public Set<Property<?>> getSupportedProperties(Value<?>... values);
	public Validator<ComplexContent> createValidator(Value<?>... values);
}
