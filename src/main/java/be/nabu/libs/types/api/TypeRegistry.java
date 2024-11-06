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
import java.util.Set;

public interface TypeRegistry {
	public SimpleType<?> getSimpleType(String namespace, String name);
	public ComplexType getComplexType(String namespace, String name);
	public Element<?> getElement(String namespace, String name);
	public Set<String> getNamespaces();
	public List<SimpleType<?>> getSimpleTypes(String namespace);
	public List<ComplexType> getComplexTypes(String namespace);
	public List<Element<?>> getElements(String namespace);
	
	// by default we assume the name is <namespace>.<name>
	// the dot is not a super strong separator, but it _is_ in line with the nabu stack method of identification
	public default Type getTypeById(String id) {
		// first search all types, the id might not match the namespace & name, we are looking for a defined type ideally
		for (String namespace : getNamespaces()) {
			for (ComplexType type : getComplexTypes(namespace)) {
				if (type instanceof DefinedType && ((DefinedType) type).getId().equals(id)) {
					return type;
				}
			}
			for (SimpleType<? >type : getSimpleTypes(namespace)) {
				if (type instanceof DefinedType && ((DefinedType) type).getId().equals(id)) {
					return type;
				}
			}
		}
		// if we haven't found it, interpret the id as a namespace followed by a name
		int index = id.lastIndexOf('.');
		String namespace = index < 0 ? null : id.substring(0, index);
		String name = index < 0 ? id : id.substring(index + 1);
		SimpleType<?> simpleType = getSimpleType(namespace, name);
		if (simpleType != null) {
			return simpleType;
		}
		return getComplexType(namespace, name);
	}
}