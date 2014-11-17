package be.nabu.libs.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	
	@SuppressWarnings("unchecked")
	public SimpleTypeWrapper getWrapper() {
		if (wrappers.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				wrappers.addAll((List<SimpleTypeWrapper>) declaredMethod.invoke(null, SimpleTypeWrapper.class));
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
			if (wrappers.isEmpty()) {
				ServiceLoader<SimpleTypeWrapper> serviceLoader = ServiceLoader.load(SimpleTypeWrapper.class);
				for (SimpleTypeWrapper wrapper : serviceLoader) {
					wrappers.add(wrapper);
				}
			}
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
