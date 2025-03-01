/*
* Copyright (C) 2014 Alexander Verbruggen
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.types.api;

/**
 * There are data types that are sensitive to marshalling in different ways:
 * 
 * - streams: if you marshal a stream, you read it out and it can no longer be used, if you are relaying this data to another party that's intentional, 
 * 		if however you are inspecting the pipeline, this should not be done as it disrupts the further flow of the logic
 * 		This means we set it to SOMETIMES
 * - mime content types: multiparts tend to have circular references to one another (parent to child, child to parent), excel sheets are another example of this
 * 		mime content types _also_ have internal streams which may suffer the stream problem from partial marshalling (suppose we detect and ignore circular references)
 * 		these are never (?) sent to a third party in their raw format, they are memory-only representation of the underlying resource
 * 		to communicate these to a third party, you will need a dedicated service to transform it into something transmittable
 * 		This means we set it to NEVER
 * 
 * if you currently create a new email part _with_ attachment (multipart and all), then trace over it, you will get a stackoverflow error from the xml marshaller
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
