package be.nabu.libs.types;

import java.util.List;

import be.nabu.libs.types.api.DefinedSimpleType;
import be.nabu.libs.types.api.SimpleTypeWrapper;

public class MultipleSimpleTypeWrapper implements SimpleTypeWrapper {

	private List<SimpleTypeWrapper> wrappers;
	
	public MultipleSimpleTypeWrapper(List<SimpleTypeWrapper> wrappers) {
		this.wrappers = wrappers;
	}
	
	@Override
	public <T> DefinedSimpleType<T> wrap(Class<T> object) {
		DefinedSimpleType<T> wrapped = null;
		for (SimpleTypeWrapper wrapper : wrappers) {
			wrapped = wrapper.wrap(object);
			if (wrapped != null)
				break;
		}
		return wrapped;
	}
}
