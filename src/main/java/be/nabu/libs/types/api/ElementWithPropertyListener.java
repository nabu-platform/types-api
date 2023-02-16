package be.nabu.libs.types.api;

import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.PropertyChangeListener;

public interface ElementWithPropertyListener<T> extends Element<T> {
	public <P> void registerPropertyListener(Property<P> property, PropertyChangeListener<P> listener);
	public <P> void unregisterPropertyListener(Property<P> property, PropertyChangeListener<P> listener);
}
