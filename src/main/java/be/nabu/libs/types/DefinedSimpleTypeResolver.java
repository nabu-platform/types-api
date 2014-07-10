package be.nabu.libs.types;

import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class DefinedSimpleTypeResolver implements DefinedTypeResolver {

	private SimpleTypeWrapper simpleTypeWrapper;
	
	public DefinedSimpleTypeResolver(SimpleTypeWrapper simpleTypeWrapper) {
		this.simpleTypeWrapper = simpleTypeWrapper;
	}
	
	@Override
	public DefinedType resolve(String id) {
		try {
			// if there is a simple type that can handle it, wrap it and send it back
			return simpleTypeWrapper == null ? null : simpleTypeWrapper.wrap(Class.forName(id));
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
