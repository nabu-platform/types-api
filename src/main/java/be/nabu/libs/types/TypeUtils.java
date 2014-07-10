package be.nabu.libs.types;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import be.nabu.libs.property.ValueUtils;
import be.nabu.libs.property.api.ComparableProperty;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.types.api.Attribute;
import be.nabu.libs.types.api.BeanConvertible;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexContentConvertible;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.api.RestrictableComplexType;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.api.TypeInstance;
import be.nabu.libs.validator.api.ValidationMessage;
import be.nabu.libs.validator.api.ValidationMessage.Severity;
import be.nabu.libs.validator.api.Validator;

public class TypeUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> T getAsBean(ComplexContent content, Class<T> beanClass) {
		if (content instanceof BeanConvertible) {
			T converted = ((BeanConvertible) content).asBean(beanClass);
			if (converted != null)
				return converted;
		}
		Type beanType = DefinedTypeResolverFactory.getInstance().getResolver().resolve(beanClass.getName());
		// it is a valid subset, create a proxy
		if (isSubset(new BaseTypeInstance(content.getType()), new BaseTypeInstance(beanType)))
			return (T) Proxy.newProxyInstance(beanClass.getClassLoader(), new Class<?> [] { beanClass, ComplexContentConvertible.class }, new ComplexContentInvocationHandler(content));
		throw new IllegalArgumentException("Can not cast the complex content to " + beanClass);
	}
	
	public static Element<?> getChild(ComplexType type, String path) {
		return getChild(type, new ParsedPath(path), false);
	}
	
	/**
	 * A local child is a child that belongs to this type specifically, in other words it is not inherited from a parent
	 */
	public static Element<?> getLocalChild(ComplexType type, String name) {
		ParsedPath parsed = new ParsedPath(name);
		if (parsed.getChildPath() != null)
			throw new IllegalArgumentException(name + " does not point to a local child");
		return getChild(type, parsed, true);
	}
	
	private static Element<?> getChild(ComplexType type, ParsedPath path, boolean localOnly) {
		Element<?> requestedChild = null;
		// check if the child is one defined by this complex type
		for (Element<?> child : type) {
			if (child.getName().equals(path.getName())) {
				requestedChild = child;
				break;
			}
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
		else if (!localOnly && type.getSuperType() != null && type.getSuperType() instanceof ComplexType)
			return getChild((ComplexType) type.getSuperType(), path, localOnly);
		else
			return null;
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
	
	public static Collection<Element<?>> getAllChildren(final ComplexType type) {
		return getAllUniqueChildren(type).values();
	}
	
	public static ComplexTypeValidator createComplexValidator(ComplexType type) {
		return new ComplexTypeValidator(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isSubset(TypeInstance subsetInstance, TypeInstance broaderInstance) {
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
			for (Element<?> broaderChild : getAllChildren((ComplexType) broaderInstance.getType())) {
				Element<?> subsetChild = ((ComplexType) subsetInstance.getType()).get(broaderChild.getName());
				if (subsetChild == null)
					return false;
				else if (!isSubset(subsetChild, broaderChild))
					return false;
			}
		}
		return true;
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
			for (Element<?> child : typeInPath) {
				// if a child type restricts a parent type, it MUST be cast-compatible
				if (children.containsKey(child.getName()) && !child.getType().equals(children.get(child.getName()).getType()) && getUpcastPath(child.getType(), children.get(child.getName()).getType()).isEmpty())
					throw new IllegalStateException("A child type restricts a parent type with an invalid restriction");
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
			if (method.getName().equals("asComplexContent") && args.length == 0)
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
				return content.get(name);
			}
			else if (method.getName().startsWith("set")) {
				// only methods that do not take parameters
				if (method.getParameterTypes().length != 1)
					throw new UnsupportedOperationException("The method " + method.getName() + " need a value argument");
				String name = method.getName().substring(3).trim();
				content.set(name, args[0]);
				return null;
			}
			throw new UnsupportedOperationException();
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
		public List<ValidationMessage> validate(Object complexInstance) {
			ComplexContent instance = complexInstance instanceof ComplexContent ? (ComplexContent) complexInstance : convert(complexInstance);
			
			List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
			if (instance.getType() != null) {
				if (!instance.getType().equals(type) && getUpcastPath(instance.getType(), type).isEmpty())
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
				List<ValidationMessage> childMessages = null;
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
							for (Object object : collection) {
								// stop if errors are detected, this allows for large docs to fail fast
								if (stopOnError) {
									boolean hasError = false;
									for (ValidationMessage message : childMessages) {
										if (message.getSeverity() == Severity.ERROR) {
											hasError = true;
											break;
										}
									}
									if (hasError)
										break;
								}
								childMessages.addAll(singleValidator.validate(object));
							}
						}
					}
					else if (singleValidator != null)
						childMessages = singleValidator.validate(value);
					
					if (childMessages != null) {
						for (ValidationMessage message : childMessages)
							message.addContext(child.getName());
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
}
