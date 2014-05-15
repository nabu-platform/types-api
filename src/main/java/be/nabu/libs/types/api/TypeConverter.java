package be.nabu.libs.types.api;

public interface TypeConverter {
	public <T> T convert(Object instance, TypeInstance from, TypeInstance to);
	public boolean canConvert(TypeInstance from, TypeInstance to);
}
