package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.DefinedTypeResolver;

public class DefinedTypeResolverFactory {

	private static DefinedTypeResolverFactory instance;
	
	public static DefinedTypeResolverFactory getInstance() {
		if (instance == null)
			instance = new DefinedTypeResolverFactory();
		return instance;
	}
	
	private List<DefinedTypeResolver> resolvers = new ArrayList<DefinedTypeResolver>();
	
	public DefinedTypeResolver getResolver() {
		if (resolvers.isEmpty()) {
			ServiceLoader<DefinedTypeResolver> serviceLoader = ServiceLoader.load(DefinedTypeResolver.class);
			for (DefinedTypeResolver resolver : serviceLoader)
				resolvers.add(resolver);
		}
		return new MultipleDefinedTypeResolver(resolvers);
	}
	
	public void addResolver(DefinedTypeResolver resolver) {
		resolvers.add(resolver);
	}
	
	public void removeResolver(DefinedTypeResolver resolver) {
		resolvers.remove(resolver);
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
}
