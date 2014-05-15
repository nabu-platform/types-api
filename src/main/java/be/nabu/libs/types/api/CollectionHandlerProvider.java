package be.nabu.libs.types.api;

import java.util.Collection;

public interface CollectionHandlerProvider<T> {
	/**
	 * You get the specific definition class in case you need more information
	 * For example an array handler would need to know the actual type definition
	 */
	public T create(Class<? extends T> definitionClass, int size);
	
	/**
	 * Given the type, return the actual component class
	 * @param type
	 * @return
	 */
	public Class<?> getComponentType(java.lang.reflect.Type type);
	
	/**
	 * Set a specific value in a collection
	 * @param collection
	 * @param index
	 * @param value
	 */
	public T set(T collection, int index, Object value);
	
	/**
	 * Retrieve a specific value from a collection
	 * @param collection
	 * @param index
	 * @return
	 */
	public Object get(T collection, int index);
	
	/**
	 * Deletes a specific value from a collection, returning the new collection (if any)
	 * @param collection
	 * @param index
	 * @return
	 */
	public T delete(T collection, int index);
	
	/**
	 * The class that it can handle. Note that this likely also applies to subclasses
	 */
	public Class<T> getCollectionClass();
	
	/**
	 * Return as an actual collection of items
	 * This allows collection-based validation
	 * @return
	 */
	public Collection<?> getAsCollection(T collection);
}
