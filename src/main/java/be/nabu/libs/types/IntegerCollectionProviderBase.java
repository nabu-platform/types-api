package be.nabu.libs.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.CollectionHandlerProvider;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.api.Marshallable;

public abstract class IntegerCollectionProviderBase<T> implements CollectionHandlerProvider<T, Integer> {

	private Class<T> collectionClass;
	
	public IntegerCollectionProviderBase(Class<T> collectionClass) {
		this.collectionClass = collectionClass;
	}
	
	@Override
	public Class<T> getCollectionClass() {
		return collectionClass;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Integer unmarshalIndex(String index, T collection) {
		if (index == null) {
			return null;
		}
		else if (index.matches("^[0-9]+$")) {
			return Integer.parseInt(index);
		}
		// if we have a collection and a non-numeric key, we assume it refers to the primary key inside a complex object
		else if (collection != null) {
			index = index.trim();
			// might be encapsulated as a string
			if (index.startsWith("\"") && index.endsWith("\"")) {
				index = index.substring(1, index.length() - 1);
			}
			else if (index.startsWith("'") && index.endsWith("'")) {
				index = index.substring(1, index.length() - 1);
			}
			Element<?> primary = null;
			int counter = 0;
			for (Object single : getAsIterable(collection)) {
				if (single != null) {
					ComplexContent wrapped = single instanceof ComplexContent ? ((ComplexContent) single) : ComplexContentWrapperFactory.getInstance().getWrapper().wrap(single); 
					if (wrapped != null) {
						if (primary == null) {
							child: for (Element<?> child : TypeUtils.getAllChildren(wrapped.getType())) {
								for (Value<?> value : child.getProperties()) {
									// can't reference the property directly
									if ("primaryKey".equals(value.getProperty().getName())) {
										primary = child;
										break child;
									}
								}
							}
							if (primary == null) {
								throw new IllegalStateException("Can not find primary key to resolve index '" + index + "' in: " + single);
							}
							else if (!(primary.getType() instanceof Marshallable)) {
								throw new IllegalStateException("Primary key is not marshallable: " + primary.getName());
							}
						}
						// we don't have the converter logic available to convert the index to the primary key, but we _can_ stringify the key
						Object primaryKeyValue = wrapped.get(primary.getName());
						String marshal = ((Marshallable) primary.getType()).marshal(primaryKeyValue, primary.getProperties());
						System.out.println("checking if " + primaryKeyValue + " == " + marshal);
						if (index.equals(marshal)) {
							return counter;
						}
					}
				}
				counter++;
			}
//			throw new ArrayIndexOutOfBoundsException("The index is not available in the list: " + index);
			// returning no index at all is inconsistent with the previous behavior (where an invalid number would throw an exception and any other number would be parsed)
			// instead we return a position beyond the list size, meaning it does not target a particular entry
			return counter;
		}
		else {
			throw new IllegalArgumentException("Invalid index: " + index);
		}
		// the old code
//		return index == null ? null : new Integer(index);
	}

	@Override
	public String marshalIndex(Integer index) {
		return index == null ? null : index.toString();
	}

	protected Collection<Integer> generateIndexes(int size) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			indexes.add(i);
		}
		return indexes;
	}

	@Override
	public Class<Integer> getIndexClass() {
		return Integer.class;
	}
	
}
