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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;

public class SPIDefinedTypeResolver implements DefinedTypeResolver {

	private static DefinedTypeResolver resolver;
	
	@Override
	public DefinedType resolve(String id) {
		return getResolver().resolve(id);
	}

	private DefinedTypeResolver getResolver() {
		if (resolver == null) {
			synchronized(this) {
				if (resolver == null) {
					List<DefinedTypeResolver> resolvers = new ArrayList<DefinedTypeResolver>();
					ServiceLoader<DefinedTypeResolver> serviceLoader = ServiceLoader.load(DefinedTypeResolver.class);
					for (DefinedTypeResolver resolver : serviceLoader) {
						resolvers.add(resolver);
					}
					resolver = new MultipleDefinedTypeResolver(resolvers);
				}
			}
		}
		return resolver;
	}
}
