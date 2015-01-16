package be.nabu.libs.types;

import java.util.HashMap;
import java.util.Map;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class DefinedSimpleTypeResolver implements DefinedTypeResolver {

	private SimpleTypeWrapper simpleTypeWrapper;
	private Map<String, Class<?>> resolvedClasses = new HashMap<String, Class<?>>();
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper) {
		this.simpleTypeWrapper = simpleTypeWrapper;
	}
	
	@Override
	public DefinedType resolve(String id) {
		try {
			if (!resolvedClasses.containsKey(id)) {
				synchronized(resolvedClasses) {
					if (!resolvedClasses.containsKey(id)) {
						resolvedClasses.put(id, Thread.currentThread().getContextClassLoader().loadClass(id));
					}
				}
			}
			// if there is a simple type that can handle it, wrap it and send it back
			return simpleTypeWrapper == null ? null : simpleTypeWrapper.wrap(resolvedClasses.get(id));
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
