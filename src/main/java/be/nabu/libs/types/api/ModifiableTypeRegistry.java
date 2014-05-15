package be.nabu.libs.types.api;

public interface ModifiableTypeRegistry extends TypeRegistry {
	public void register(Element<?>...elements);
	public void register(ComplexType...types);
	public void register(SimpleType<?>...types);
	public void register(TypeRegistry...registries);
}
