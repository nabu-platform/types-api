package be.nabu.libs.types.api;

/**
 * All simple types that can be ad hoc wrapped around a class, must be definable
 * The id they return should be the name of the class they wrap around
 * There should only be one wrapper per object type in an environment
 */
public interface SimpleTypeWrapper {
	public <T> DefinedSimpleType<T> wrap(Class<T> object);
}
