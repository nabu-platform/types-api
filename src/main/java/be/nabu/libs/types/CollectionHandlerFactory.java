package be.nabu.libs.types;

import java.util.Iterator;
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
	
	public CollectionHandler getHandler() {
		if (handler == null) {
			ServiceLoader<CollectionHandler> serviceLoader = ServiceLoader.load(CollectionHandler.class);
			Iterator<CollectionHandler> iterator = serviceLoader.iterator();
			if (iterator.hasNext())
				handler = iterator.next();
			else
				handler = new CollectionHandlerStub();
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
