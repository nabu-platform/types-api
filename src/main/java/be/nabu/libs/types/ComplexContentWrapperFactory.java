package be.nabu.libs.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.libs.types.api.ComplexContentWrapper;

public class ComplexContentWrapperFactory {

	private static ComplexContentWrapperFactory instance;
	
	public static ComplexContentWrapperFactory getInstance() {
		if (instance == null)
			instance = new ComplexContentWrapperFactory();
		return instance;
	}
	
	private List<ComplexContentWrapper<?>> wrappers = new ArrayList<ComplexContentWrapper<?>>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComplexContentWrapper getWrapper() {
		if (wrappers.isEmpty()) {
			synchronized(wrappers) {
				List<ComplexContentWrapper<?>> wrappers = new ArrayList<ComplexContentWrapper<?>>();
				try {
					// let's try this with custom service loading based on a configuration
					Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
					Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
					wrappers.addAll((List<ComplexContentWrapper<?>>) declaredMethod.invoke(null, ComplexContentWrapper.class));
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
					ServiceLoader<ComplexContentWrapper> serviceLoader = ServiceLoader.load(ComplexContentWrapper.class);
					for (ComplexContentWrapper<?> wrapper : serviceLoader) {
						wrappers.add(wrapper);
					}
				}
				this.wrappers.addAll(wrappers);
			}
		}
		return new MultipleComplexContentWrapper(wrappers);
	}
	
	public void addWrapper(ComplexContentWrapper<?> wrapper) {
		wrappers.add(wrapper);
	}
	
	public void removeWrapper(ComplexContentWrapper<?> wrapper) {
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
