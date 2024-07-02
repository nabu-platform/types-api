package be.nabu.libs.types.api;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "key", "value" })
public interface KeyRawValuePair {
	public String getKey();
	public Object getValue();
}
