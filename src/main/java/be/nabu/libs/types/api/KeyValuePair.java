package be.nabu.libs.types.api;

import javax.xml.bind.annotation.XmlType;

/**
 * This interface was added here as it is used so often in the context of types it needs to be generally accessible
 */
@XmlType(propOrder = { "key", "value" })
public interface KeyValuePair {
	public String getKey();
	public String getValue();
}
