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

import be.nabu.libs.types.api.ComplexContentWrapper;

public class ComplexContentWrapperFactory {

	private static ComplexContentWrapperFactory instance;
	
	public static ComplexContentWrapperFactory getInstance() {
		if (instance == null)
			instance = new ComplexContentWrapperFactory();
		return instance;
	}
	
	private List<ComplexContentWrapper<?>> wrappers = new ArrayList<ComplexContentWrapper<?>>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComplexContentWrapper getWrapper() {
		if (wrappers.isEmpty()) {
			synchronized(wrappers) {
				if (wrappers.isEmpty()) {
					List<ComplexContentWrapper<?>> wrappers = new ArrayList<ComplexContentWrapper<?>>();
					try {
						// let's try this with custom service loading based on a configuration
						Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
						Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
						wrappers.addAll((List<ComplexContentWrapper<?>>) declaredMethod.invoke(null, ComplexContentWrapper.class));
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
					if (wrappers.isEmpty()) {
						ServiceLoader<ComplexContentWrapper> serviceLoader = ServiceLoader.load(ComplexContentWrapper.class);
						for (ComplexContentWrapper<?> wrapper : serviceLoader) {
							wrappers.add(wrapper);
						}
					}
					this.wrappers.addAll(wrappers);
				}
			}
		}
		return new MultipleComplexContentWrapper(wrappers);
	}
	
	public void addWrapper(ComplexContentWrapper<?> wrapper) {
		wrappers.add(wrapper);
	}
	
	public void removeWrapper(ComplexContentWrapper<?> wrapper) {
		wrappers.remove(wrapper);
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
