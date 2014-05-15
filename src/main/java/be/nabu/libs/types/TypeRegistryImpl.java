package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.api.ModifiableTypeRegistry;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.TypeRegistry;

public class TypeRegistryImpl implements ModifiableTypeRegistry {

	private Map<String, Map<String, Element<?>>> elements = new HashMap<String, Map<String, Element<?>>>();
	private Map<String, Map<String, ComplexType>> complexTypes = new HashMap<String, Map<String, ComplexType>>();
	private Map<String, Map<String, SimpleType<?>>> simpleTypes = new HashMap<String, Map<String, SimpleType<?>>>();
	private List<TypeRegistry> registries = new ArrayList<TypeRegistry>();
	
	@Override
	public SimpleType<?> getSimpleType(String namespace, String name) {
		return simpleTypes.containsKey(namespace)
			? simpleTypes.get(namespace).get(name)
			: getNestedSimpleType(namespace, name);
	}
	
	private SimpleType<?> getNestedSimpleType(String namespace, String name) {
		for (TypeRegistry registry : registries) {
			SimpleType<?> simpleType = registry.getSimpleType(namespace, name);
			if (simpleType != null)
				return simpleType;
		}
		return null;	
	}
	
	@Override
	public ComplexType getComplexType(String namespace, String name) {
		return complexTypes.containsKey(namespace)
			? complexTypes.get(namespace).get(name)
			: getNestedComplexType(namespace, name);
	}

	private ComplexType getNestedComplexType(String namespace, String name) {
		for (TypeRegistry registry : registries) {
			ComplexType complexType = registry.getComplexType(namespace, name);
			if (complexType != null)
				return complexType;
		}
		return null;	
	}
	
	@Override
	public Element<?> getElement(String namespace, String name) {
		return elements.containsKey(namespace)
			? elements.get(namespace).get(name)
			: getNestedElement(namespace, name);
	}
	
	private Element<?> getNestedElement(String namespace, String name) {
		for (TypeRegistry registry : registries) {
			Element<?> element = registry.getElement(namespace, name);
			if (element != null)
				return element;
		}
		return null;
	}

	@Override
	public void register(Element<?>...elements) {
		for (Element<?> element : elements) {
			if (!this.elements.containsKey(element.getNamespace()))
				this.elements.put(element.getNamespace(), new HashMap<String, Element<?>>());
			if (this.elements.get(element.getNamespace()).containsKey(element.getName()))
				throw new IllegalArgumentException("The element " + element.getNamespace() + " # " + element.getName() + " already exists");
			this.elements.get(element.getNamespace()).put(element.getName(), element);
		}
	}

	@Override
	public void register(ComplexType...types) {
		for (ComplexType type : types) {
			if (!this.complexTypes.containsKey(type.getNamespace()))
				this.complexTypes.put(type.getNamespace(), new HashMap<String, ComplexType>());
			if (this.complexTypes.get(type.getNamespace()).containsKey(type.getName()))
				throw new IllegalArgumentException("The complexType " + type.getNamespace() + " # " + type.getName() + " already exists");
			this.complexTypes.get(type.getNamespace()).put(type.getName(), type);
		}
	}

	@Override
	public void register(SimpleType<?>...types) {
		for (SimpleType<?> type : types) {
			if (!this.simpleTypes.containsKey(type.getNamespace()))
				this.simpleTypes.put(type.getNamespace(), new HashMap<String, SimpleType<?>>());
			if (this.simpleTypes.get(type.getNamespace()).containsKey(type.getName()))
				throw new IllegalArgumentException("The simpleType " + type.getNamespace() + " # " + type.getName() + " already exists");
			this.simpleTypes.get(type.getNamespace()).put(type.getName(), type);
		}
	}

	@Override
	public void register(TypeRegistry... registries) {
		this.registries.addAll(Arrays.asList(registries));		
	}

	@Override
	public Set<String> getNamespaces() {
		Set<String> set = new HashSet<String>();
		set.addAll(simpleTypes.keySet());
		set.addAll(complexTypes.keySet());
		set.addAll(elements.keySet());
		return set;
	}

	@Override
	public List<SimpleType<?>> getSimpleTypes(String namespace) {
		return new ArrayList<SimpleType<?>>(simpleTypes.get(namespace).values());
	}

	@Override
	public List<ComplexType> getComplexTypes(String namespace) {
		return new ArrayList<ComplexType>(complexTypes.get(namespace).values());
	}

	@Override
	public List<Element<?>> getElements(String namespace) {
		return new ArrayList<Element<?>>(elements.get(namespace).values());
	}

}
