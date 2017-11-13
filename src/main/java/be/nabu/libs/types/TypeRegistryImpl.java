package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.api.ModifiableTypeRegistry;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.TypeRegistry;

public class TypeRegistryImpl implements ModifiableTypeRegistry {

	/**
	 * Allows you to switch whether included registries take priority over locally defined elements
	 */
	private boolean prioritizeIncludes;
	private Map<String, Map<String, Element<?>>> elements = new HashMap<String, Map<String, Element<?>>>();
	private Map<String, Map<String, ComplexType>> complexTypes = new HashMap<String, Map<String, ComplexType>>();
	private Map<String, Map<String, SimpleType<?>>> simpleTypes = new HashMap<String, Map<String, SimpleType<?>>>();
	private List<TypeRegistry> registries = new ArrayList<TypeRegistry>();
	private boolean useTypeIds;
	
	@Override
	public SimpleType<?> getSimpleType(String namespace, String name) {
		if (prioritizeIncludes) {
			SimpleType<?> simpleType = getNestedSimpleType(namespace, name);
			if (simpleType == null && simpleTypes.containsKey(namespace)) {
				simpleType = simpleTypes.get(namespace).get(name);
			}
			return simpleType;
		}
		else {
			return simpleTypes.containsKey(namespace) && simpleTypes.get(namespace).containsKey(name)
				? simpleTypes.get(namespace).get(name)
				: getNestedSimpleType(namespace, name);
		}
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
		if (prioritizeIncludes) {
			ComplexType complexType = getNestedComplexType(namespace, name);
			if (complexType == null && complexTypes.containsKey(namespace)) {
				complexType = complexTypes.get(namespace).get(name);
			}
			return complexType;
		}
		else {
			return complexTypes.containsKey(namespace) && complexTypes.get(namespace).containsKey(name)
				? complexTypes.get(namespace).get(name)
				: getNestedComplexType(namespace, name);
		}
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
		if (prioritizeIncludes) {
			Element<?> element = getNestedElement(namespace, name);
			if (element == null && elements.containsKey(namespace)) {
				element = elements.get(namespace).get(name);
			}
			return element;
		}
		else {
			return elements.containsKey(namespace) && elements.get(namespace).containsKey(name)
				? elements.get(namespace).get(name)
				: getNestedElement(namespace, name);
		}
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
			String name = useTypeIds && type instanceof DefinedType ? ((DefinedType) type).getId() : type.getName();
			if (!this.complexTypes.containsKey(type.getNamespace()))
				this.complexTypes.put(type.getNamespace(), new HashMap<String, ComplexType>());
			if (this.complexTypes.get(type.getNamespace()).containsKey(name))
				throw new IllegalArgumentException("The complexType " + type.getNamespace() + " # " + name + " already exists");
			this.complexTypes.get(type.getNamespace()).put(name, type);
		}
	}

	@Override
	public void register(SimpleType<?>...types) {
		for (SimpleType<?> type : types) {
			String name = useTypeIds && type instanceof DefinedType ? ((DefinedType) type).getId() : type.getName();
			if (!this.simpleTypes.containsKey(type.getNamespace()))
				this.simpleTypes.put(type.getNamespace(), new HashMap<String, SimpleType<?>>());
			if (this.simpleTypes.get(type.getNamespace()).containsKey(name))
				throw new IllegalArgumentException("The simpleType " + type.getNamespace() + " # " + name + " already exists");
			this.simpleTypes.get(type.getNamespace()).put(name, type);
		}
	}

	@Override
	public void register(TypeRegistry... registries) {
		this.registries.addAll(Arrays.asList(registries));		
	}
	
	public void unregister(ComplexType...types) {
		for (ComplexType type : types) {
			String name = useTypeIds && type instanceof DefinedType ? ((DefinedType) type).getId() : type.getName();
			if (this.complexTypes.containsKey(type.getNamespace())) {
				this.complexTypes.get(type.getNamespace()).remove(name);
			}
		}
	}
	
	public void unregister(SimpleType<?>...types) {
		for (SimpleType<?> type : types) {
			String name = useTypeIds && type instanceof DefinedType ? ((DefinedType) type).getId() : type.getName();
			if (this.simpleTypes.containsKey(type.getNamespace())) {
				this.simpleTypes.get(type.getNamespace()).remove(name);
			}
		}
	}
	
	public void unregister(Element<?>...elements) {
		for (Element<?> element : elements) {
			if (this.elements.containsKey(element.getNamespace())) {
				this.elements.get(element.getNamespace()).remove(element.getName());
			}
		}
	}

	@Override
	public Set<String> getNamespaces() {
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(simpleTypes.keySet());
		set.addAll(complexTypes.keySet());
		set.addAll(elements.keySet());
		for (TypeRegistry nested : registries) {
			set.addAll(nested.getNamespaces());
		}
		return set;
	}

	@Override
	public List<SimpleType<?>> getSimpleTypes(String namespace) {
		ArrayList<SimpleType<?>> result = new ArrayList<SimpleType<?>>();
		if (simpleTypes.containsKey(namespace)) {
			result.addAll(simpleTypes.get(namespace).values());
		}
		for (TypeRegistry nested : registries) {
			result.addAll(nested.getSimpleTypes(namespace));
		}
		return result;
	}

	@Override
	public List<ComplexType> getComplexTypes(String namespace) {
		ArrayList<ComplexType> result = new ArrayList<ComplexType>();
		if (complexTypes.containsKey(namespace)) { 
			result.addAll(complexTypes.get(namespace).values());
		}
		for (TypeRegistry nested : registries) {
			result.addAll(nested.getComplexTypes(namespace));
		}
		return result;
	}

	@Override
	public List<Element<?>> getElements(String namespace) {
		ArrayList<Element<?>> result = new ArrayList<Element<?>>();
		if (elements.containsKey(namespace)) { 
			result.addAll(elements.get(namespace).values());
		}
		for (TypeRegistry nested : registries) {
			result.addAll(nested.getElements(namespace));
		}
		return result;
	}

	public boolean isPrioritizeIncludes() {
		return prioritizeIncludes;
	}
	public void setPrioritizeIncludes(boolean prioritizeIncludes) {
		this.prioritizeIncludes = prioritizeIncludes;
	}

	public boolean isUseTypeIds() {
		return useTypeIds;
	}

	public void setUseTypeIds(boolean useTypeIds) {
		this.useTypeIds = useTypeIds;
	}

}
