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
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.CollectionHandler;

public class CollectionHandlerFactory {
	
	private static CollectionHandlerFactory instance;
	
	public static CollectionHandlerFactory getInstance() {
		if (instance == null)
			instance = new CollectionHandlerFactory();
		return instance;
	}
	
	private CollectionHandler handler;
	
	@SuppressWarnings("unchecked")
	public CollectionHandler getHandler() {
		if (handler == null) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				List<CollectionHandler> handlers = (List<CollectionHandler>) declaredMethod.invoke(null, CollectionHandler.class);
				if (!handlers.isEmpty()) {
					handler = handlers.get(0);
				}
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
			if (handler == null) {
				ServiceLoader<CollectionHandler> serviceLoader = ServiceLoader.load(CollectionHandler.class);
				Iterator<CollectionHandler> iterator = serviceLoader.iterator();
				if (iterator.hasNext())
					handler = iterator.next();
				else
					handler = new CollectionHandlerStub();
			}
		}
		return handler;
	}
	
	public void setHandler(CollectionHandler handler) {
		this.handler = handler;
	}
	
	public void unsetHandler(CollectionHandler handler) {
		handler = null;
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
