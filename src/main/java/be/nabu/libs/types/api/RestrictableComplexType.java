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

/**
 * A complex type can restrict a child element from a supertype, for example if you have a child element of an undefined type (to allow for an API)
 * you might want to restrict it to a certain type for an implementation
 * 
 * If you retrieve the child of a complex type you will get the restricted version instead of the broad version
 * You can check if it has been restricted, if so you can deduce the original requirement by getting the supertype and getting the child from there.
 */
public interface RestrictableComplexType extends ComplexType {
	public Element<?> getRestriction(String childName);
}
