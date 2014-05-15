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
}