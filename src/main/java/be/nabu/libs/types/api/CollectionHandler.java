package be.nabu.libs.types.api;

public interface CollectionHandler {
	public <T> CollectionHandlerProvider<T> getHandler(Class<T> clazz);
}
