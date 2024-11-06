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
