package be.nabu.libs.types.api;

public interface BeanConvertible extends ComplexContent {
	/**
	 * If you can not convert the current complex content into a bean of the indicated
	 * class you should return null. There are other ways to convert.
	 * @param target
	 * @return
	 */
	public <T> T asBean(Class<T> target);
}
