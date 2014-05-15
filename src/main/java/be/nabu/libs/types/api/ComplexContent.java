package be.nabu.libs.types.api;

public interface ComplexContent {
	public ComplexType getType();
	public void set(String path, Object value);
	public Object get(String path);
}
