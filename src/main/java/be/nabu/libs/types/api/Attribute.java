package be.nabu.libs.types.api;

public interface Attribute<T> extends Element<T> {
	
	@Override
	public SimpleType<T> getType();
	
}
