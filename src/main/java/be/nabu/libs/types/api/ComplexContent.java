package be.nabu.libs.types.api;

public interface ComplexContent {
	
	// if the value you are trying to set is null and we have to create parent instances to explicitly set that null, should we?
	public static boolean CREATE_PARENT_FOR_NULL_VALUE = Boolean.parseBoolean(System.getProperty("be.nabu.types.createParentForNullValue", "false"));
	
	public ComplexType getType();
	public void set(String path, Object value);
	public Object get(String path);
}
