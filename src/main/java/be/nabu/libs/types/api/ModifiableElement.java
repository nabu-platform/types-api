package be.nabu.libs.types.api;

public interface ModifiableElement<T> extends ModifiableTypeInstance, Element<T> {
	public void setParent(ComplexType type);
}
