package be.nabu.libs.types;

import java.util.HashMap;
import java.util.Map;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class DefinedSimpleTypeResolver implements DefinedTypeResolver {

	private SimpleTypeWrapper simpleTypeWrapper;
	private Map<String, DefinedType> definedTypes = new HashMap<String, DefinedType>();
	private ClassLoader[] loaders;
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper, ClassLoader...loaders) {
		this.simpleTypeWrapper = simpleTypeWrapper;
		this.loaders = loaders;
	}
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper) {
		this.simpleTypeWrapper = simpleTypeWrapper;
	}
	
	@Override
	public DefinedType resolve(String id) {
		try {
			if (simpleTypeWrapper != null && !definedTypes.containsKey(id)) {
				synchronized(definedTypes) {
					if (!definedTypes.containsKey(id)) {
						Class<?> loadClass = null;
						if (loaders != null) {
							for (ClassLoader loader : loaders) {
								try {
									loadClass = loader.loadClass(id);
								}
								catch (ClassNotFoundException e) {
									continue;
								}
							}
						}
						if (loadClass == null) {
							loadClass = Thread.currentThread().getContextClassLoader().loadClass(id);
						}
						definedTypes.put(id, simpleTypeWrapper.wrap(loadClass));
					}
				}
			}
			// if there is a simple type that can handle it, wrap it and send it back
			return definedTypes.get(id);
		}
		catch (ClassNotFoundException e) {
			// not a class, leave it to someone else
			return null;
		}
	}

	public SimpleTypeWrapper getSimpleTypeWrapper() {
		return simpleTypeWrapper;
	}
}
