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

import java.util.List;

import be.nabu.libs.types.api.DefinedSimpleType;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class MultipleSimpleTypeWrapper implements SimpleTypeWrapper {

	private List<SimpleTypeWrapper> wrappers;
	
	public MultipleSimpleTypeWrapper(List<SimpleTypeWrapper> wrappers) {
		this.wrappers = wrappers;
	}
	
	@Override
	public <T> DefinedSimpleType<T> wrap(Class<T> object) {
		DefinedSimpleType<T> wrapped = null;
		for (SimpleTypeWrapper wrapper : wrappers) {
			wrapped = wrapper.wrap(object);
			if (wrapped != null)
				break;
		}
		return wrapped;
	}

	@Override
	public DefinedSimpleType<?> getByName(String name) {
		for (SimpleTypeWrapper wrapper : wrappers) {
			DefinedSimpleType<?> byName = wrapper.getByName(name);
			if (byName != null) {
				return byName;
			}
		}
		return null;
	}
}
