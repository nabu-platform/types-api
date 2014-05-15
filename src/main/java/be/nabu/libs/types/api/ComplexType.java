package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Value;

/**
 * A complex type is an item that can contain other items
 * 
 * @author alex
 *
 */
public interface ComplexType extends Type, Iterable<Element<?>> {
	
	/**
	 * Used to indicate that you want the value element of a simple complex type
	 */
	public static final String SIMPLE_TYPE_VALUE = "$value";
	
	/**
	 * Gets a specific child by its path name
	 * @param path
	 * @return
	 */
	public Element<?> get(String path);

	/**
	 * Creates a new instance of this complex type
	 * @return
	 */
	public ComplexContent newInstance();

	public Boolean isAttributeQualified(Value<?>...values);
	public Boolean isElementQualified(Value<?>...values);
	
	/**
	 * Return all the groups that belong to this complex type
	 * @return
	 */
	public Group [] getGroups();
	
}
