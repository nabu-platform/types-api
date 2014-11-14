package be.nabu.libs.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	
	@SuppressWarnings("unchecked")
	public DefinedTypeResolver getResolver() {
		if (resolvers.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				resolvers.addAll((List<DefinedTypeResolver>) declaredMethod.invoke(null, DefinedTypeResolver.class));
			}
			catch (ClassNotFoundException e) {
				// ignore, the framework is not present
			}
			catch (NoSuchMethodException e) {
				// corrupt framework?
				throw new RuntimeException(e);
			}
			catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				// ignore
			}
			catch (InvocationTargetException e) {
				// ignore
			}
			if (resolvers.isEmpty()) {
				// it will first try to resolve it as a simple type
				// this will take care of e.g. boolean, string,...
				resolvers.add(new DefinedSimpleTypeResolver(SimpleTypeWrapperFactory.getInstance().getWrapper()));
				resolvers.add(new SPIDefinedTypeResolver());
			}
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
