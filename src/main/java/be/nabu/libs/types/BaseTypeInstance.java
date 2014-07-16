package be.nabu.libs.types;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.PropertyWithDefault;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.ModifiableTypeInstance;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.api.TypeInstance;

public class BaseTypeInstance implements ModifiableTypeInstance {

	private Map<Property<?>, Value<?>> properties = new LinkedHashMap<Property<?>, Value<?>>();
	
	private Type type;
	
	public BaseTypeInstance(Type type, Value<?>...properties) {
		this.type = type;
		setProperty(properties);
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> Value<S> getProperty(Property<S> property) {
		return (Value<S>) properties.get(property);
	}
	
	@Override
	public void setProperty(Value<?>...values) {
		for (Value<?> value : values) {
			if (value.getProperty() instanceof PropertyWithDefault && value.getValue() != null && value.getValue().equals(((PropertyWithDefault<?>) value.getProperty()).getDefault()))
				properties.remove(value.getProperty());
			else
				properties.put(value.getProperty(), value);
		}
	}

	@Override
	public Value<?>[] getProperties() {
		Map<Property<?>, Value<?>> values = new HashMap<Property<?>, Value<?>>();
		if (type != null) {
			for (Value<?> value : type.getProperties())
				values.put(value.getProperty(), value);
			values.putAll(properties);
		}
		return values.values().toArray(new Value<?>[values.size()]);
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof TypeInstance 
			&& ((TypeInstance) object).getType().equals(getType())
			&& java.util.Arrays.asList(((TypeInstance) object).getProperties()).equals(java.util.Arrays.asList(getProperties()));
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}
	
}
