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

import java.util.List;

import be.nabu.libs.validator.api.ValidationMessage;

public interface ModifiableComplexType extends ComplexType, ModifiableType {
	/**
	 * The return value should be an empty list if the element was added successfully
	 * If the element could not be added, it should contain a list of validation messages that explain the reason(s) why
	 * @param element
	 * @return
	 */
	public List<ValidationMessage> add(Element<?> element);
	
	/**
	 * Remove an element
	 * @param element
	 */
	public void remove(Element<?> element);
	
	/**
	 * Add a group to this complex type
	 * @param group
	 * @return
	 */
	public List<ValidationMessage> add(Group group);
	
	/**
	 * Remove a group from this complex type
	 * @param group
	 */
	public void remove(Group group);

	public void setName(String name);
	public void setNamespace(String namespace);
}
