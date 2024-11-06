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

import be.nabu.libs.types.api.TypeConverter;
import be.nabu.libs.types.api.TypeInstance;

public class MultipleTypeConverter implements TypeConverter {

	private List<TypeConverter> converters;
	
	public MultipleTypeConverter(List<TypeConverter> converters) {
		this.converters = converters;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object instance, TypeInstance from, TypeInstance to) {
		Object converted = null;
		for (TypeConverter converter : converters) {
			converted = converter.convert(instance, from, to);
			if (converted != null)
				break;
		}
		return (T) converted;
	}

	@Override
	public boolean canConvert(TypeInstance from, TypeInstance to) {
		for (TypeConverter converter : converters) {
			if (converter.canConvert(from, to))
				return true;
		}
		return false;
	}

}
