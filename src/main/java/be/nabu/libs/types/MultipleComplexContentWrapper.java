package be.nabu.libs.types;

import java.util.List;

import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexContentWrapper;

public class MultipleComplexContentWrapper implements ComplexContentWrapper<Object> {

	private List<ComplexContentWrapper<?>> wrappers;
	
	public MultipleComplexContentWrapper(List<ComplexContentWrapper<?>> wrappers) {
		this.wrappers = wrappers;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ComplexContent wrap(Object object) {
		if (object == null) {
			return null;
		}
		ComplexContentWrapper closestWrapper = null;
		for (ComplexContentWrapper wrapper : wrappers) {
			if (wrapper.getInstanceClass().isAssignableFrom(object.getClass())) {
				if (closestWrapper == null || closestWrapper.getInstanceClass().isAssignableFrom(wrapper.getInstanceClass())) {
					closestWrapper = wrapper;
				}
			}
		}
		return closestWrapper == null ? null : closestWrapper.wrap(object);
	}

	@Override
	public Class<Object> getInstanceClass() {
		return Object.class;
	}
}
