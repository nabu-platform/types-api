package be.nabu.libs.types.api;

public interface TypedCollectionHandlerProvider<T, V> extends CollectionHandlerProvider<T, V> {
	public boolean isCompatible(T collection, Type valueType);
}
