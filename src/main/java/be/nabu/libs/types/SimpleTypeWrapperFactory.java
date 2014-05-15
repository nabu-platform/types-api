package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.SimpleTypeWrapper;

public class SimpleTypeWrapperFactory {

	private static SimpleTypeWrapperFactory instance;
	
	public static SimpleTypeWrapperFactory getInstance() {
		if (instance == null)
			instance = new SimpleTypeWrapperFactory();
		return instance;
	}
	
	private List<SimpleTypeWrapper> wrappers = new ArrayList<SimpleTypeWrapper>();
	
	public SimpleTypeWrapper getWrapper() {
		if (wrappers.isEmpty()) {
			ServiceLoader<SimpleTypeWrapper> serviceLoader = ServiceLoader.load(SimpleTypeWrapper.class);
			for (SimpleTypeWrapper wrapper : serviceLoader)
				wrappers.add(wrapper);
		}
		return new MultipleSimpleTypeWrapper(wrappers);
	}
	
	public void addWrapper(SimpleTypeWrapper wrapper) {
		wrappers.add(wrapper);
	}
	
	public void removeWrapper(SimpleTypeWrapper wrapper) {
		wrappers.remove(wrapper);
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
