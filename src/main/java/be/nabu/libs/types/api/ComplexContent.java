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

public interface ComplexContent {
	
	// if the value you are trying to set is null and we have to create parent instances to explicitly set that null, should we?
	public static boolean CREATE_PARENT_FOR_NULL_VALUE = Boolean.parseBoolean(System.getProperty("be.nabu.types.createParentForNullValue", "true"));
	
	public ComplexType getType();
	public void set(String path, Object value);
	public Object get(String path);
	
	// Check if a complex content has a value for a given path
	// By default we assume if the type supports it, that the content has _a_ value (could be null)
	// If you have smarter implementations that can track changes, you can be more specific in your answer and state whether someone explicitly set a value
	public default boolean has(String path) {
		return getType().get(path) != null;
	}
	/**
	 * Because we now have "has" logic, the difference between an explicit null value and a non-existing value becomes more important
	 * If you can't offer the difference (e.g. java objects have no way of expressing this), deleting means setting to null
	 * However for patching reasons we want to be able to differentiate if the library supports it
	 */
	public default void delete(String path) {
		set(path, null);
	}
}
