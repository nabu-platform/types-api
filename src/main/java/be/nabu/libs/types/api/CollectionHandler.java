package be.nabu.libs.types.api;

public interface CollectionHandler {
	public <T, V> CollectionHandlerProvider<T, V> getHandler(Class<T> clazz);
}
