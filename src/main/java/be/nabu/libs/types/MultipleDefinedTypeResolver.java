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

package be.nabu.libs.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;

public class MultipleDefinedTypeResolver implements DefinedTypeResolver {

	private List<DefinedTypeResolver> resolvers;
	
	private Map<String, DefinedType> definedTypes = new HashMap<String, DefinedType>();
	
	public MultipleDefinedTypeResolver(List<DefinedTypeResolver> resolvers) {
		this.resolvers = resolvers;
	}
	
	@Override
	public DefinedType resolve(String id) {
		if (!definedTypes.containsKey(id)) {
			synchronized(definedTypes) {
				if (!definedTypes.containsKey(id)) {
					DefinedType type = null;
					for (DefinedTypeResolver resolver : resolvers) {
						type = resolver.resolve(id);
						if (type != null) {
							definedTypes.put(id, type);
							break;
						}
					}
				}
			}
		}
		return definedTypes.get(id);
	}

	public List<DefinedTypeResolver> getResolvers() {
		return resolvers;
	}
	
}
