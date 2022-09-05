package be.nabu.libs.types.api;

import java.util.List;
import java.util.Set;

public interface TypeRegistry {
	public SimpleType<?> getSimpleType(String namespace, String name);
	public ComplexType getComplexType(String namespace, String name);
	public Element<?> getElement(String namespace, String name);
	public Set<String> getNamespaces();
	public List<SimpleType<?>> getSimpleTypes(String namespace);
	public List<ComplexType> getComplexTypes(String namespace);
	public List<Element<?>> getElements(String namespace);
	
	// by default we assume the name is <namespace>.<name>
	// the dot is not a super strong separator, but it _is_ in line with the nabu stack method of identification
	public default Type getTypeById(String id) {
		int index = id.lastIndexOf('.');
		String namespace = index < 0 ? null : id.substring(0, index);
		String name = index < 0 ? id : id.substring(index + 1);
		SimpleType<?> simpleType = getSimpleType(namespace, name);
		if (simpleType != null) {
			return simpleType;
		}
		return getComplexType(namespace, name);
	}
}