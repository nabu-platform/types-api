package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.List;

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
			// it will first try to resolve it as a simple type
			// this will take care of e.g. boolean, string,...
			resolvers.add(new DefinedSimpleTypeResolver(SimpleTypeWrapperFactory.getInstance().getWrapper()));
			resolvers.add(new SPIDefinedTypeResolver());
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
