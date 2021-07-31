package be.nabu.libs.types.api;

/**
 * There are data types that are sensitive to marshalling in different ways:
 * 
 * - streams: if you marshal a stream, you read it out and it can no longer be used, if you are relaying this data to another party that's intentional, 
 * 		if however you are inspecting the pipeline, this should not be done as it disrupts the further flow of the logic
 * 		This means we set it to SOMETIMES
 * - mime content types: they tend to have circular references to one another (parent to child, child to parent), excel sheets are another example of this
 * 		mime content types _also_ have internal streams which may suffer the stream problem from partial marshalling (suppose we detect and ignore circular references)
 * 		these are never (?) sent to a third party in their raw format, they are memory-only representation of the underlying resource
 * 		to communicate these to a third party, you will need a dedicated service to transform it into something transmittable
 * 		This means we set it to NEVER
 * 
 * We can't simply annotate the impacted types as they can reside in third party libraries, which is why I went for the factory method
 * The default assumption is that the result is ALWAYS unless otherwise specified
 */
public interface MarshalRuleProvider {
	public enum MarshalRule {
		ALWAYS,
		SOMETIMES,
		NEVER
	}
	public MarshalRule getMarshalRule(Class<?> clazz);
}
