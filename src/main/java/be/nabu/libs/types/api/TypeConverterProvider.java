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
 * Allows you to convert an object instance from one type to another
 * The parameters may be necessary because they contain information about the type
 */
public interface TypeConverterProvider {
	
	public <T> T convert(Object instance, Value<?> [] sourceParameters, Value<?>...targetParameters);
	
	public Type getSourceType();
	public Type getTargetType();
	
}
