package be.nabu.libs.types;

import java.util.List;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;

public class MultipleDefinedTypeResolver implements DefinedTypeResolver {

	private List<DefinedTypeResolver> resolvers;
	
	public MultipleDefinedTypeResolver(List<DefinedTypeResolver> resolvers) {
		this.resolvers = resolvers;
	}
	
	@Override
	public DefinedType resolve(String id) {
		DefinedType type = null;
		for (DefinedTypeResolver resolver : resolvers) {
			type = resolver.resolve(id);
			if (type != null)
				break;
		}
		return type;
	}

}
