package be.nabu.libs.types;

import be.nabu.libs.types.api.CollectionHandler;
import be.nabu.libs.types.api.CollectionHandlerProvider;

public class CollectionHandlerStub implements CollectionHandler {

	@Override
	public <T> CollectionHandlerProvider<T> getHandler(Class<T> clazz) {
		return null;
	}

}
