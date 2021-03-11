package be.nabu.libs.types.api;

public interface JavaClassWrapper<T> extends Type {
	public Class<T> getWrappedClass();
}
