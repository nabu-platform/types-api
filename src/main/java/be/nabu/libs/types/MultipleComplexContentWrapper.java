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

import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexContentWrapper;

public class MultipleComplexContentWrapper implements ComplexContentWrapper<Object> {

	private List<ComplexContentWrapper<?>> wrappers;
	
	public MultipleComplexContentWrapper(List<ComplexContentWrapper<?>> wrappers) {
		this.wrappers = wrappers;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ComplexContent wrap(Object object) {
		if (object == null) {
			return null;
		}
		else if (object instanceof ComplexContent) {
			return (ComplexContent) object;
		}
		ComplexContentWrapper closestWrapper = null;
		for (ComplexContentWrapper wrapper : wrappers) {
			if (wrapper.getInstanceClass().isAssignableFrom(object.getClass())) {
				if (closestWrapper == null || closestWrapper.getInstanceClass().isAssignableFrom(wrapper.getInstanceClass())) {
					closestWrapper = wrapper;
				}
			}
		}
		return closestWrapper == null ? null : closestWrapper.wrap(object);
	}

	@Override
	public Class<Object> getInstanceClass() {
		return Object.class;
	}

	public List<ComplexContentWrapper<?>> getWrappers() {
		return wrappers;
	}
	
}
