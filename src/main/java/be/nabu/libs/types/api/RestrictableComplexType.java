package be.nabu.libs.types.api;

/**
 * A complex type can restrict a child element from a supertype, for example if you have a child element of an undefined type (to allow for an API)
 * you might want to restrict it to a certain type for an implementation
 * 
 * If you retrieve the child of a complex type you will get the restricted version instead of the broad version
 * You can check if it has been restricted, if so you can deduce the original requirement by getting the supertype and getting the child from there.
 */
public interface RestrictableComplexType extends ComplexType {
	public Element<?> getRestriction(String childName);
}
