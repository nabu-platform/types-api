package be.nabu.libs.types.api;

public interface SynchronizableTypeRegistry extends TypeRegistry {
	// whether or not the target type is synchronizable
	// for example in a data model there might be types that do not need to be synchronized to a database
	public boolean isSynchronizable(Type type);
}
