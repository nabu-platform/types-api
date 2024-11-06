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
import java.util.Map;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class DefinedSimpleTypeResolver implements DefinedTypeResolver {

	private SimpleTypeWrapper simpleTypeWrapper;
	private Map<String, DefinedType> definedTypes = new HashMap<String, DefinedType>();
	private ClassLoader[] loaders;
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper, ClassLoader...loaders) {
		this.simpleTypeWrapper = simpleTypeWrapper;
		this.loaders = loaders;
	}
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper) {
		this.simpleTypeWrapper = simpleTypeWrapper;
	}
	
	@Override
	public DefinedType resolve(String id) {
		try {
			if (simpleTypeWrapper != null && !definedTypes.containsKey(id)) {
				synchronized(definedTypes) {
					if (!definedTypes.containsKey(id)) {
						Class<?> loadClass = null;
						if (loaders != null) {
							for (ClassLoader loader : loaders) {
								try {
									loadClass = loader.loadClass(id);
								}
								catch (ClassNotFoundException e) {
									continue;
								}
							}
						}
						if (loadClass == null) {
							loadClass = Thread.currentThread().getContextClassLoader().loadClass(id);
						}
						definedTypes.put(id, simpleTypeWrapper.wrap(loadClass));
					}
				}
			}
			// if there is a simple type that can handle it, wrap it and send it back
			return definedTypes.get(id);
		}
		catch (ClassNotFoundException e) {
			// not a class, leave it to someone else
			return null;
		}
	}

	public SimpleTypeWrapper getSimpleTypeWrapper() {
		return simpleTypeWrapper;
	}
}
