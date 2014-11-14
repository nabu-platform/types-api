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
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
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
