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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.TypeConverter;

public class TypeConverterFactory {
	
	private static TypeConverterFactory instance;
	
	public static TypeConverterFactory getInstance() {
		if (instance == null)
			instance = new TypeConverterFactory();
		return instance;
	}
	
	private List<TypeConverter> converters = new ArrayList<TypeConverter>();
	
	@SuppressWarnings("unchecked")
	public TypeConverter getConverter() {
		if (converters.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				converters.addAll((List<TypeConverter>) declaredMethod.invoke(null, TypeConverter.class));
			}
			catch (ClassNotFoundException e) {
				// ignore, the framework is not present
			}
			catch (NoSuchMethodException e) {
				// corrupt framework?
				throw new RuntimeException(e);
			}
			catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				// ignore
			}
			catch (InvocationTargetException e) {
				// ignore
			}
			if (converters.isEmpty()) {
				ServiceLoader<TypeConverter> serviceLoader = ServiceLoader.load(TypeConverter.class);
				for (TypeConverter converter : serviceLoader) {
					converters.add(converter);
				}
			}
		}
		return new MultipleTypeConverter(converters);
	}
	
	public void addConverter(TypeConverter converter) {
		converters.add(converter);
	}
	
	public void removeConverter(TypeConverter converter) {
		converters.remove(converter);
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
}
