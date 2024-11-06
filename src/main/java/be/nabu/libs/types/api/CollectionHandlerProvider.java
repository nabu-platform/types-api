/*
* Copyright (C) 2014 Alexander Verbruggen
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.types.api;

import java.util.Collection;

/**
 * This handles a collection of type T (e.g. a java.util.List)
 * The "V" indicates the type of value needed (e.g. Integer)
 */
public interface CollectionHandlerProvider<T, V> {
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
	public T set(T collection, V index, Object value);
	
	/**
	 * Retrieve a specific value from a collection
	 * @param collection
	 * @param index
	 * @return
	 */
	public Object get(T collection, V index);
	
	/**
	 * Deletes a specific value from a collection, returning the new collection (if any)
	 * @param collection
	 * @param index
	 * @return
	 */
	public T delete(T collection, V index);
	
	/**
	 * The class that it can handle. Note that this likely also applies to subclasses
	 */
	public Class<T> getCollectionClass();
	
	/**
	 * The indexes that it can handle
	 */
	public Class<V> getIndexClass();
	
	/**
	 * Return as an actual collection of items
	 * This allows collection-based validation
	 * @return
	 */
	public Collection<?> getAsCollection(T collection);
	
	/**
	 * This returns the collection as an iterable where we can stream over the data
	 * By default it uses the getAsCollection()
	 */
	public default Iterable<?> getAsIterable(T collection) {
		return getAsCollection(collection);
	}
	
	/**
	 * Returns the indexes as a collection, you can then repeatedly call get()
	 * @return
	 */
	public Collection<V> getIndexes(T collection);
	
	/**
	 * The index has to be marshallable/unmarshallable
	 * This is because the paths that we use are always strings
	 */
	public V unmarshalIndex(String index, T collection);
	public String marshalIndex(V index);
}
