package be.nabu.libs.types;

import be.nabu.libs.types.api.CollectionHandlerProvider;

public abstract class IntegerCollectionProviderBase<T> implements CollectionHandlerProvider<T, Integer> {

	private Class<T> collectionClass;
	
	public IntegerCollectionProviderBase(Class<T> collectionClass) {
		this.collectionClass = collectionClass;
	}
	
	@Override
	public Class<T> getCollectionClass() {
		return collectionClass;
	}

	@Override
	public Integer unmarshalIndex(String index) {
		return index == null ? null : new Integer(index);
	}

	@Override
	public String marshalIndex(Integer index) {
		return index == null ? null : index.toString();
	}

}
