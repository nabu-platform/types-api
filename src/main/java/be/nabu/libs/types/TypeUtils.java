package be.nabu.libs.types;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import be.nabu.libs.property.ValueUtils;
import be.nabu.libs.property.api.ComparableProperty;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.Attribute;
import be.nabu.libs.types.api.BeanConvertible;
import be.nabu.libs.types.api.CollectionHandlerProvider;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexContentConvertible;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.DefinedType;
import be.nabu.libs.types.api.DefinedTypeResolver;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.api.JavaClassWrapper;
import be.nabu.libs.types.api.Marshallable;
import be.nabu.libs.types.api.RestrictableComplexType;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.SneakyEditableBeanInstance;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.api.TypeInstance;
import be.nabu.libs.validator.api.ContextUpdatableValidation;
import be.nabu.libs.validator.api.Validation;
import be.nabu.libs.validator.api.ValidationMessage;
import be.nabu.libs.validator.api.ValidationMessage.Severity;
import be.nabu.libs.validator.api.Validator;

public class TypeUtils {
	
	private static Map<Class<?>, Class<?>> boxedTypes = new HashMap<Class<?>, Class<?>>();
	
	static {
		boxedTypes.put(boolean.class, Boolean.class);
		boxedTypes.put(byte.class, Byte.class);
		boxedTypes.put(short.class, Short.class);
		boxedTypes.put(char.class, Character.class);
		boxedTypes.put(int.class, Integer.class);
		boxedTypes.put(long.class, Long.class);
		boxedTypes.put(float.class, Float.class);
	    boxedTypes.put(double.class, Double.class);
	}
	
	public static Class<?> box(Class<?> clazz) {
		return clazz.isPrimitive() ? boxedTypes.get(clazz) : clazz;
	}
	
	public static <T> T getAsBean(ComplexContent content, Class<T> beanClass) {
		return getAsBean(content, beanClass, DefinedTypeResolverFactory.getInstance().getResolver());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getAsBean(ComplexContent content, Class<T> beanClass, DefinedTypeResolver resolver) {
		if (content instanceof BeanConvertible) {
			T converted = ((BeanConvertible) content).asBean(beanClass);
			if (converted != null)
				return converted;
		}
		Type beanType = resolver.resolve(beanClass.getName());
		// it is a valid subset, create a proxy
		if (isSubset(new BaseTypeInstance(content.getType()), new BaseTypeInstance(beanType)))
			return (T) Proxy.newProxyInstance(beanClass.getClassLoader(), new Class<?> [] { beanClass, ComplexContentConvertible.class, SneakyEditableBeanInstance.class }, new ComplexContentInvocationHandler(content));
		throw new IllegalArgumentException("Can not cast the complex content to " + beanClass);
	}
	
	public static Element<?> getChild(ComplexType type, String path) {
		return getChild(type, ParsedPath.parse(path), false);
	}
	
	/**
	 * A local child is a child that belongs to this type specifically, in other words it is not inherited from a parent
	 */
	public static Element<?> getLocalChild(ComplexType type, String name) {
		ParsedPath parsed = ParsedPath.parse(name);
		if (parsed.getChildPath() != null)
			throw new IllegalArgumentException(name + " does not point to a local child");
		return getChild(type, parsed, true);
	}
	
	private static Element<?> getChild(ComplexType type, ParsedPath path, boolean localOnly) {
		Element<?> requestedChild = null;
		// check if the child is one defined by this complex type
		Element<?> aliasedChild = null;
		for (Element<?> child : type) {
			if (child.getName().equals(path.getName())) {
				requestedChild = child;
				break;
			}
			else {
				for (Value<?> value : child.getProperties()) {
					if (value.getProperty().getName().equals("alias") && path.getName().equals(value.getValue())) {
						aliasedChild = child;
					}
				}
			}
		}
		if (requestedChild == null) {
			requestedChild = aliasedChild;
		}
		// if the type supports restrictions, check if it restricts the parent type
		if (type instanceof RestrictableComplexType) {
			Element<?> restrictedChild = ((RestrictableComplexType) type).getRestriction(path.getName());
			if (restrictedChild != null)
				requestedChild = restrictedChild;
		}
		// if we have found the requested child, use that
		if (requestedChild != null) {
			if (path.getChildPath() == null)
				return requestedChild;
			else if (requestedChild.getType() instanceof ComplexType)
				return getChild((ComplexType) requestedChild.getType(), path.getChildPath(), localOnly);
			else
				throw new IllegalArgumentException("The child " + path.getName() + " is not a complex type and can not be recursed");
		}
		// not found in the current complex type, check if it is in an extended one
		else if (!localOnly && type.getSuperType() != null && type.getSuperType() instanceof ComplexType) {
			// if we have a property that restricts from the parent, don't cascade
			for (Value<?> value : type.getProperties()) {
				if (value.getProperty().getName().equals("restrict") && value.getValue() instanceof String) {
					if (Arrays.asList(value.getValue().toString().split("[\\s]*,[\\s]*")).indexOf(path.getName()) >= 0) {
						return null;
					}
				}
			}
			return getChild((ComplexType) type.getSuperType(), path, localOnly);
		}
		else
			return null;
	}
	
	public static boolean isExtension(Type possibleExtension, Type fromParent) {
		return possibleExtension != null && fromParent != null && (isSameType(possibleExtension, fromParent) || !getUpcastPath(possibleExtension, fromParent).isEmpty());
	}
	
	public static List<Type> getUpcastPath(Type fromChildType, Type toParentType) {		
		List<Type> path = new ArrayList<Type>();
		
		while (fromChildType != null && !fromChildType.equals(toParentType)) {
			fromChildType = fromChildType.getSuperType();
			if (fromChildType != null)
				path.add(fromChildType);
		}

		// ok we have walked the entire path and not found the parent type
		// clear the list or people will think it's an actual upcast path
		if (fromChildType == null || !fromChildType.equals(toParentType))
			path.clear();
		
		return path;
	}
	
	public static Iterator<Element<?>> getAllChildrenIterator(ComplexType type) {
		return new RecursiveIterator(type);
	}
	
	public static List<Element<?>> getLocalChildren(ComplexType type) {
		List<Element<?>> children = new ArrayList<Element<?>>();
		for (Element<?> child : type) {
			children.add(child);
		}
		return children;
	}
	
	public static Collection<Element<?>> getAllChildren(final ComplexType type) {
		return getAllUniqueChildren(type).values();
	}
	
	public static ComplexTypeValidator createComplexValidator(ComplexType type) {
		return new ComplexTypeValidator(type);
	}

	public static boolean isSameType(Type typeA, Type typeB) {
		if (typeA.equals(typeB)) {
			return true;
		}
		else if (typeA instanceof DefinedType && typeB instanceof DefinedType && ((DefinedType) typeA).getId().equals(((DefinedType) typeB).getId())) {
			return true;
		}
		return false;
	}
	
	public static boolean isSubset(TypeInstance subsetInstance, TypeInstance broaderInstance) {
		return isSubset(subsetInstance, broaderInstance, new HashMap<ComplexType, List<ComplexType>>());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isSubset(TypeInstance subsetInstance, TypeInstance broaderInstance, Map<ComplexType, List<ComplexType>> alreadyChecked) {
		// only complex types can be completely unrelated subsets of one another
		// simple types can be subsets, but only if they are directly related to the original, otherwise you will get classcast exceptions at some point
		// this is because complex types are exposed through the generic ComplexContent interface while simple types are actual java objects like java.lang.String
		if ((subsetInstance.getType() instanceof ComplexType && broaderInstance.getType() instanceof ComplexType)
				|| (subsetInstance.getType() instanceof SimpleType && broaderInstance.getType() instanceof SimpleType && isExtension(subsetInstance.getType(), broaderInstance.getType()))) {
			// we need to check all the properties of the broader instance
			for (Property<?> property : broaderInstance.getType().getSupportedProperties(subsetInstance.getProperties())) {
				Object subsetValue = ValueUtils.getValue(property, subsetInstance.getProperties());
				Object broaderValue = ValueUtils.getValue(property, broaderInstance.getProperties());
	
				// use the available subset method
				if (property instanceof ComparableProperty) {
					if (!((ComparableProperty) property).isSubset(subsetValue, broaderValue))
						return false;
				}
				// they must both be null or both be the same
				else if (subsetValue == null ^ broaderValue == null || (subsetValue != null && !subsetValue.equals(broaderValue)))
					return false;
				else if (subsetInstance.getType() instanceof SimpleType && !((SimpleType) broaderInstance.getType()).getInstanceClass().isAssignableFrom(((SimpleType) subsetInstance.getType()).getInstanceClass())) 
					return false;
			}
			// if we are dealing with complex types, check all the children of the broader type
			if (subsetInstance.getType() instanceof ComplexType) {
				if (!(broaderInstance.getType() instanceof ComplexType))
					return false;
				if (!alreadyChecked.containsKey(subsetInstance.getType())) {
					alreadyChecked.put((ComplexType) subsetInstance.getType(), new ArrayList<ComplexType>());
				}
				if (alreadyChecked.get(subsetInstance.getType()).contains(broaderInstance.getType())) {
					return true;
				}
				else {
					alreadyChecked.get(subsetInstance.getType()).add((ComplexType) broaderInstance.getType());
				}
				for (Element<?> broaderChild : getAllChildren((ComplexType) broaderInstance.getType())) {
					Element<?> subsetChild = ((ComplexType) subsetInstance.getType()).get(broaderChild.getName());
					if (subsetChild == null)
						return false;
					else if (!isSubset(subsetChild, broaderChild, alreadyChecked))
						return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private static class RecursiveIterator implements Iterator<Element<?>> {

		private Iterator<Element<?>> current;
		private List<ComplexType> path = new ArrayList<ComplexType>();
		private Iterator<ComplexType> type;
		
		public RecursiveIterator(ComplexType type) {
			// first generate a full path of this type
			while (type instanceof ComplexType) {
				path.add(type);
				if (type.getSuperType() instanceof ComplexType)
					type = (ComplexType) type.getSuperType();
				else
					break;
			}
			// reverse it so the lowest parent is first (elements must be in order!)
			Collections.reverse(path);
			this.type = path.iterator();
			current = this.type.next().iterator();
		}
		
		@Override
		public boolean hasNext() {
			if (current.hasNext())
				return true;
			else if (!type.hasNext())
				return false;
			else {
				while (!current.hasNext() && type.hasNext())
					current = type.next().iterator();
				return current.hasNext();
			}
		}
		
		@Override
		public Element<?> next() {
			// makes sure that the current iterator is placed correctly
			if (hasNext())
				return current.next();
			else
				throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			current.remove();
		}
		
	}
	
	/**
	 * This method takes into account that child types may restrict parent types
	 * If a child type redefines an element, the parent type is overwritten
	 * @param type
	 * @return
	 */
	private static LinkedHashMap<String, Element<?>> getAllUniqueChildren(ComplexType type) {
		// first generate a full path of this type
		List<ComplexType> path = new ArrayList<ComplexType>();
		while (type instanceof ComplexType) {
			path.add(type);
			if (type.getSuperType() instanceof ComplexType)
				type = (ComplexType) type.getSuperType();
			else
				break;
		}
		// reverse it so the lowest parent is first (elements must be in order!)
		Collections.reverse(path);
		LinkedHashMap<String, Element<?>> children = new LinkedHashMap<String, Element<?>>();
		for (ComplexType typeInPath : path) {
			// first apply restrictions so you can do non-compatible types if necessary
			// also: you don't want to be able to restrict your own children, just delete it
			for (Value<?> value : typeInPath.getProperties()) {
				if (value.getProperty() != null && value.getProperty().getName() != null && value.getProperty().getName().equals("restrict") && value.getValue() != null) {
					List<String> restricted = Arrays.asList(value.getValue().toString().split("[\\s]*,[\\s]*"));
					Iterator<String> iterator = children.keySet().iterator();
					while (iterator.hasNext()) {
						if (restricted.indexOf(iterator.next()) >= 0) {
							iterator.remove();
						}
					}
				}
			}
			for (Element<?> child : typeInPath) {
				// if a child type restricts a parent type, it MUST be cast-compatible
				// @2019-09-03: let's be more lenient at this point. it is mostly when trying to express the types in for example XSD that this will cause issues
				// so we move the responsibility of checking it to there as it poses no real problem for structures, json schema...
//				if (children.containsKey(child.getName()) && !child.getType().equals(children.get(child.getName()).getType()) && getUpcastPath(child.getType(), children.get(child.getName()).getType()).isEmpty()) {
//					// nasty workaround for bean types
//					// the problem is with generics etc, it is hard to validate whether an override is correct, however because it is a java bean, we can assume the compiler checked it all and that it is indeed valid
//					// however we have no visibility of the bean type implementation at this point so...
//					if (!child.getType().getClass().getName().contains("BeanType") && !typeInPath.getClass().getName().contains("BeanType")) {
//						throw new IllegalStateException("A child type restricts a parent type with an invalid restriction for element: " + child.getName() + " in " + typeInPath);
//					}
//				}
				children.put(child.getName(), child);
			}
		}
		return children;
	}
		
	private static class ComplexContentInvocationHandler implements InvocationHandler {

		private ComplexContent content;
		
		public ComplexContentInvocationHandler(ComplexContent content) {
			this.content = content;
		}
		
		@Override
		public Object invoke(Object arg0, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("asComplexContent") && (args == null || args.length == 0))
				return content;
			else if (method.getName().startsWith("get")) {
				// only methods that do not take parameters
				if (method.getParameterTypes().length > 0)
					throw new UnsupportedOperationException("The method " + method.getName() + " does not take any arguments");
				// this is inherent in every object, do not use
				else if (method.getName().equals("getClass"))
					return arg0.getClass();
				String name = method.getName().substring(3).trim();
				if (name.isEmpty())
					throw new UnsupportedOperationException();
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
				Object returnValue = content.get(name);
				if (returnValue == null) {
					return null;
				}
				else if (returnValue instanceof ComplexContent && !method.getReturnType().isAssignableFrom(returnValue.getClass())) {
					return TypeUtils.getAsBean((ComplexContent) returnValue, method.getReturnType());
				}
				else if (returnValue instanceof Collection) {
					CollectionHandlerProvider<? extends Object, Object> handler = CollectionHandlerFactory.getInstance().getHandler().getHandler(returnValue.getClass());
					Class<?> componentType = handler.getComponentType(method.getGenericReturnType());
					List<Object> list = new ArrayList<Object>();
					for (Object child : (Collection<?>) returnValue) {
						list.add(child instanceof ComplexContent ? TypeUtils.getAsBean((ComplexContent) child, componentType) : child);
					}
					return list;
				}
				else {
					return returnValue;
				}
			}
			else if (method.getName().startsWith("set")) {
				// only methods that do not take parameters
				if (method.getParameterTypes().length != 1)
					throw new UnsupportedOperationException("The method " + method.getName() + " need a value argument");
				String name = method.getName().substring(3).trim();
				content.set(name, args[0]);
				return null;
			}
			// sneaky set!
			else if (method.getName().equals("__set") && args.length == 2) {
				content.set((String) args[0], args[1]);
				return null;
			}
			else if (method.getName().equals("toString") && (args == null || args.length == 0)) {
				return "Proxy for: " + content;
			}
			throw new UnsupportedOperationException("Could not find method " + method + " in content: " + content);
		}
		
	}
	
	public static class ComplexTypeValidator implements Validator<Object> {

		private ComplexType type;
		
		/**
		 * If a complex content element has no type, do we verify the actual type?
		 */
		private boolean forceTypeValidation = true;
		
		/**
		 * This determines whether or not the validation will stop when an error is encountered
		 */
		private boolean stopOnError = false;
		
		public ComplexTypeValidator(ComplexType type) {
			this.type = type;
		}
		
		/**
		 * If an object comes in that is not a complex content, this method is invoked to convert it
		 * @param instance
		 * @return
		 */
		protected ComplexContent convert(Object instance) {
			return (ComplexContent) instance;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public List<Validation<?>> validate(Object complexInstance) {
			ComplexContent instance = complexInstance instanceof ComplexContent ? (ComplexContent) complexInstance : convert(complexInstance);
			
			List<Validation<?>> messages = new ArrayList<Validation<?>>();
			if (instance == null) {
				return messages;
			}
			if (instance.getType() != null) {
				// java.lang.Object is special (as always!)
				boolean isObject = type instanceof JavaClassWrapper && ((JavaClassWrapper<?>) type).getWrappedClass().equals(Object.class);
				if (!isObject && !instance.getType().equals(type) && getUpcastPath(instance.getType(), type).isEmpty())
					messages.add(new ValidationMessage(Severity.ERROR, "The actual type " + instance.getType() + " is not related to the expected type " + type));
			}
			else if (forceTypeValidation)
				messages.add(new ValidationMessage(Severity.ERROR, "Could not verify the type of complex content"));
			
			// if the type is also a simple type, verify the simple type content
			if (type instanceof SimpleType) {
				Element simpleElement = type.get(ComplexType.SIMPLE_TYPE_VALUE);
				try {
					Object value = instance.get(ComplexType.SIMPLE_TYPE_VALUE);
					messages.addAll(simpleElement.validate(value));
				}
				catch (RuntimeException e) {
					messages.add(new ValidationMessage(Severity.ERROR, "Could not parse simple value of " + type.getName() + ": " + e.getMessage()));
				}
			}
			
			// still continue with validation, could be on best effort basis if the type is unchecked
			for (Element<?> child : getAllChildren(type)) {
				List<? extends Validation<?>> childMessages = null;
				Validator singleValidator = child.getType().createValidator(child.getProperties());
				try {
					Object value = instance.get((child instanceof Attribute ? "@" : "") + child.getName());
					// if it's an array, create a collection around it
					if (value instanceof Object[])
						value = Arrays.asList((Object[]) value);
					if (value instanceof Collection) {
						Collection<?> collection = (Collection<?>) value;
						// check if we have a collection validator
						Validator<Collection<?>> collectionValidator = child.getType().createCollectionValidator(child.getProperties());
						if (collectionValidator != null)
							childMessages = collectionValidator.validate(collection);
						// initialize for the single one
						else
							childMessages = new ArrayList<ValidationMessage>();
						if (singleValidator != null) {
							int index = 0;
							for (Object object : collection) {
								// stop if errors are detected, this allows for large docs to fail fast
								if (stopOnError) {
									boolean hasError = false;
									for (Validation<?> message : childMessages) {
										if (message.getSeverity() == Severity.ERROR) {
											hasError = true;
											break;
										}
									}
									if (hasError)
										break;
								}
								List localMessages = singleValidator.validate(object);
								for (Validation message : (List<Validation<?>>) localMessages) {
									if (message instanceof ContextUpdatableValidation) {
										((ContextUpdatableValidation) message).addContext(Integer.toString(index));
									}
								}
								index++;
								childMessages.addAll(localMessages);
							}
						}
					}
					else if (singleValidator != null)
						childMessages = singleValidator.validate(value);
					
					if (childMessages != null) {
						for (Validation message : childMessages) {
							if (message instanceof ContextUpdatableValidation) {
								((ContextUpdatableValidation) message).addContext(child.getName());
							}
						}
						messages.addAll(childMessages);
					}
				}
				catch(RuntimeException e) {
					messages.add(new ValidationMessage(Severity.ERROR, "Could not parse " + child.getName() + ": " + e.getMessage()));
				}
			}
			return messages;
		}

		public boolean isStopOnError() {
			return stopOnError;
		}

		public void setStopOnError(boolean stopOnError) {
			this.stopOnError = stopOnError;
		}

		@Override
		public Class<Object> getValueClass() {
			return Object.class;
		}
		
	}
	
	public static boolean isMarshallable(Type type) {
		boolean isMarshallable = true;
		if (type instanceof ComplexType) {
			for (Element<?> child : (ComplexType) type) {
				isMarshallable &= isMarshallable(child.getType());
			}
		}
		else {
			isMarshallable &= type instanceof Marshallable;
		}
		return isMarshallable;
	}
}
