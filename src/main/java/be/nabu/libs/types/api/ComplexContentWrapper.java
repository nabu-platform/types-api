package be.nabu.libs.types.api;

public interface ComplexContentWrapper<T> {
	public ComplexContent wrap(T instance);
	public Class<T> getInstanceClass();
}
