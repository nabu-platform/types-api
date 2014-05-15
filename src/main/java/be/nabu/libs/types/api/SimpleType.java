package be.nabu.libs.types.api;

/**
 * A simple type wraps around a java object that represents a single value, e.g. string, date,...
 * 
 * @author alex
 *
 */
public interface SimpleType<T> extends Type {
	public Class<T> getInstanceClass();
}