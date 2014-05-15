package be.nabu.libs.types.api;

import java.util.Set;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.validator.api.Validator;

/**
 * An element is the combination of a type and its specific contextual parameters 
 */
public interface Element<T> extends Validator<T>, TypeInstance {

	public ComplexType getParent();
	
	public String getName();
	public String getNamespace();
	
	public void setProperty(Value<?>...values);

	/**
	 * Allows the element to adjust the supported properties
	 * For example it might add some properties to those supported by the type
	 * @return
	 */
	public Set<Property<?>> getSupportedProperties();
}
