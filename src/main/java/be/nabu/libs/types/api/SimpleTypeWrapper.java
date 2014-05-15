package be.nabu.libs.types.api;

public interface SimpleTypeWrapper {
	public <T> SimpleType<T> wrap(Class<T> object);
}
