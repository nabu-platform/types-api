package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;

public class SPIDefinedTypeResolver implements DefinedTypeResolver {

	private static DefinedTypeResolver resolver;
	
	@Override
	public DefinedType resolve(String id) {
		return getResolver().resolve(id);
	}

	private DefinedTypeResolver getResolver() {
		if (resolver == null) {
			synchronized(this) {
				if (resolver == null) {
					List<DefinedTypeResolver> resolvers = new ArrayList<DefinedTypeResolver>();
					ServiceLoader<DefinedTypeResolver> serviceLoader = ServiceLoader.load(DefinedTypeResolver.class);
					for (DefinedTypeResolver resolver : serviceLoader) {
						resolvers.add(resolver);
					}
					resolver = new MultipleDefinedTypeResolver(resolvers);
				}
			}
		}
		return resolver;
	}
}
