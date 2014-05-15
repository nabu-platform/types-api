package be.nabu.libs.types;

import java.util.List;

import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.Type;

public class MultipleDefinedTypeResolver implements DefinedTypeResolver {

	private List<DefinedTypeResolver> resolvers;
	
	public MultipleDefinedTypeResolver(List<DefinedTypeResolver> resolvers) {
		this.resolvers = resolvers;
	}
	
	@Override
	public Type getType(String id) {
		Type type = null;
		for (DefinedTypeResolver resolver : resolvers) {
			type = resolver.getType(id);
			if (type != null)
				break;
		}
		return type;
	}

}
