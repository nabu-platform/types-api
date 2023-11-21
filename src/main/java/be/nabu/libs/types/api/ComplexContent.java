package be.nabu.libs.types.api;

public interface ComplexContent {
	
	// if the value you are trying to set is null and we have to create parent instances to explicitly set that null, should we?
	public static boolean CREATE_PARENT_FOR_NULL_VALUE = Boolean.parseBoolean(System.getProperty("be.nabu.types.createParentForNullValue", "false"));
	
	public ComplexType getType();
	public void set(String path, Object value);
	public Object get(String path);
	
	// Check if a complex content has a value for a given path
	// By default we assume if the type supports it, that the content has _a_ value (could be null)
	// If you have smarter implementations that can track changes, you can be more specific in your answer and state whether someone explicitly set a value
	public default boolean has(String path) {
		return getType().get(path) != null;
	}
}
