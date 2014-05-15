package be.nabu.libs.types.api;

import java.util.Collection;
import java.util.Set;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.validator.api.Validator;

/**
 * A type is a description of a data element
 * The type should implement equals() to make sure two instances of the same type equal each other 
 */
public interface Type {		
	
	public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * If it has no name (anonymous), return null
	 * @param values
	 * @return
	 */
	public String getName(Value<?>...values);
	
	/**
	 * If it has no namespace return null
	 * @param values
	 * @return
	 */
	public String getNamespace(Value<?>...values);
	
	/**
	 * Whether or not this type represents a list of things
	 * @param properties
	 * @return
	 */
	public boolean isList(Value<?>...properties);
	
	/**
	 * Given the parameters a validator should be built
	 * @param parameters
	 * @return
	 */
	public Validator<?> createValidator(Value<?>...properties);
	
	/**
	 * You can also provide a validator to check entire collections of the type
	 * @param properties
	 * @return
	 */
	public Validator<Collection<?>> createCollectionValidator(Value<?>...properties);
	
	/**
	 * Lists the supported parameters
	 * This can depend on the parameters that are currently set, e.g. xsd does not allow maxInclusive and maxExclusive to be set at the same time
	 * Every time a parameter value is set, you should ideally request the list again 
	 * @return
	 */
	public Set<Property<?>> getSupportedProperties(Value<?>...properties);
	
	/**
	 * Gets the super type that is extended by this type (if any)
	 * Returns null if it extends nothing
	 */
	public Type getSuperType();
	
	/**
	 * A type can have fixed properties that (unlike element properties) are not dependent on the instance but in effect for all instances
	 */
	public Value<?> [] getProperties();
}
