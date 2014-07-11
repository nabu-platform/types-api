package be.nabu.libs.types.api;

import java.util.List;

import be.nabu.libs.validator.api.ValidationMessage;

public interface ModifiableComplexType extends ComplexType, ModifiableType {
	/**
	 * The return value should be an empty list if the element was added successfully
	 * If the element could not be added, it should contain a list of validation messages that explain the reason(s) why
	 * @param element
	 * @return
	 */
	public List<ValidationMessage> add(Element<?> element);
	
	/**
	 * Remove an element
	 * @param element
	 */
	public void remove(Element<?> element);
	
	/**
	 * Add a group to this complex type
	 * @param group
	 * @return
	 */
	public List<ValidationMessage> add(Group group);
	
	/**
	 * Remove a group from this complex type
	 * @param group
	 */
	public void remove(Group group);

	public void setName(String name);
	public void setNamespace(String namespace);
}
