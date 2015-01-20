package be.nabu.libs.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;

public class MultipleDefinedTypeResolver implements DefinedTypeResolver {

	private List<DefinedTypeResolver> resolvers;
	
	private Map<String, DefinedType> definedTypes = new HashMap<String, DefinedType>();
	
	public MultipleDefinedTypeResolver(List<DefinedTypeResolver> resolvers) {
		this.resolvers = resolvers;
	}
	
	@Override
	public DefinedType resolve(String id) {
		if (!definedTypes.containsKey(id)) {
			synchronized(definedTypes) {
				if (!definedTypes.containsKey(id)) {
					DefinedType type = null;
					for (DefinedTypeResolver resolver : resolvers) {
						type = resolver.resolve(id);
						if (type != null) {
							definedTypes.put(id, type);
							break;
						}
					}
				}
			}
		}
		return definedTypes.get(id);
	}

}
