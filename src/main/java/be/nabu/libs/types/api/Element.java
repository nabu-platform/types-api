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
