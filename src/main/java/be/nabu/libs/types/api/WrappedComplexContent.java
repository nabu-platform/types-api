package be.nabu.libs.types.api;

public interface WrappedComplexContent<T> extends ComplexContent {
	public T getUnwrapped();
}
