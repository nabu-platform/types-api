# Description

The types API is a set of interfaces that describe how data works. 
It is meant to be the central interface for a lot of functionality that works on data types in general: querying, validation, parsing, transformation...

# API

The API is based on three concepts:

- **Simple Types**: a simple type has a singular base data representation, for instance a string, an integer, a date,…
- **Complex Types**: combines zero or more simple types and other complex types into a group, for example a complex type Person could consist of multiple simple types:
	- **Last Name**: string
	- **First Name**: string
	- **Age**: integer
- Elements: types describe what data looks like but there are a number of properties related to actual instances that can differ. For example let’s take the complex type Person from the above example, suppose we have a Company type:
	- **Name**: string
	- **Address**: string
	- **CEO**: Person
	- **Employees**: Person
In this case the CEO is one person and he must, there can however be zero or more employees: it could be a one man show (the CEO only) or it could be a multinational with thousands of employees. So at the very least we need a way to describe multiplicity. These circumstantial properties however do not change the type of Person, they are related to the context it is used in. To allow for such contextual representations, the types are wrapped in an element.
- **ComplexContent**: at runtime the simple types are of course simply instances of whatever class represents them, however complex types are exposed as ComplexContent implementations.

These concepts are of course nothing new, they correlate strongly with existing interpretations:

- **XML Schema**: has simple types, complex types & elements. The naming of the API is also based on it.
- **Java**: has native types to represent simple types and java beans that allow you to construct complex types
- **C**: has native types & structs
- ...

The API tries to strike a balance between the features and limitations of existing data formats. For example in XML Schema it is possible (in a select few cases) to use multiple elements with the same name in a complex type, however java does not support this in a class.
Note that concepts like choice, extension and (to some degree) restriction are supported.

# Why?

The problem is that java already has a lot of utilities but there is no overarching vision to unify these tools to allow them to work in different contexts.
For example there is XPath but it was designed to work on XML. Yes in Jaxen you can implement the (rather complex) Navigator interface to query other backends but it is not part of a larger whole, it works only for jaxen. 
Additionally xpath was designed for XML so uses constructs that may not be reflected everywhere and may be limited by constraints inherent to XML (e.g. the "|" operator breaks static type checks).

JAXB is awesome for binding XML to beans but suppose you are working in an environment where beans take a backseat to other data representations? 
I have worked a lot with Webmethods where a custom data format is used, JAXB is useless in that context.

This project was started because I was building a set of tools that needed to be applicable in different environments.

Currently there are implementations for:
- **XML**: there is support for exposing Document instances as ComplexContent. Additionally there is support for exposing XML Schema using ComplexType & SimpleType.
- **Java**: there is support for exposing classes as complex types and wrapping instances in ComplexContent
- **Webmethods**: there is support for exposing IData instances as ComplexContent and also support for exposing documents as ComplexType
- **Structure**: a custom data format, the original (and reference) implementation. It is more flexible than java classes because it does not need compiling and allows for additional features (like special kinds of type casting)

These types can be mixed and matched, for instance:

```java
public static void main(String...args) throws IOException {
	SimpleTypeWrapper wrapper = SimpleTypeWrapperFactory.getInstance().getWrapper();
	Structure structure = new Structure();
	structure.setName("myRoot");
	structure.setSuperType(new BeanType(Message.class));
	structure.add(new SimpleElementImpl(
		"context",
		wrapper.wrap(String.class),
		structure,
		new ValueImpl(new MinOccursProperty(), 0))
	);
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	new XSDDefinitionMarshaller().marshal(output, structure);
	System.out.println(new String(output.toByteArray()));
}

public static interface Message {
	public String getMessage();
	public int getLevel();
}
```

This will print:

```xml
<schema xmlns="http://www.w3.org/2001/XMLSchema">
	<element name="myRoot">
		<complexType>
			<sequence>
				<element name="message" nillable="true"/>
				<element name="level" type="int"/>
				<element minOccurs="0" name="context"/>
			</sequence>
		</complexType>
	</element>
</schema>
```

# Tools

Now for the important question: which tools exist based on these API’s?

## Evaluator

The evaluator is comparable to an xpath engine. In fact because we have an XML implementation I can (and have) pit it against the xpath engine as a comparison. A short overview of the advantages of the evaluator over xpath:
- Can be run on all complex content (xml, java objects, IData instances,…)
- Type checking: if you give the engine a complex type it can statically validate the rule which means you will have fewer runtime exceptions
- Return types: in xpath the developer has to tell the engine what resultset it should return (a string, a nodeset,…). The evaluator will return a deterministic result based on the query.
- Tight integration with java: you can call static methods easily in a query.
- It’s a general evaluator so you can also ask it how much "1+1" is.
- It’s actually faster. There is some documentation on a speed test in the evaluator package but the conclusion is that the evaluator is about 3 times faster than the default xpath implementation. I must admit that more testing is needed to verify that it is a consistent result but since I was aiming for speed parity, this was a nice bonus.

A short example of the evaluator (based on the speed test code):

```java
public static void main(String...args) throws Exception {
	Document largeDoc = load(new File("largedoc.xml"));
	XPath xPath = XPathFactory.newInstance().newXPath();
	XPathExpression expression = xPath.compile("metering/measurement[@volume = '171.69040']/@energy");
	System.out.println(expression.evaluate(largeDoc.getDocumentElement()));
	XMLContent content = new XMLContent(largeDoc.getDocumentElement());
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("metering/measurement[@volume = '171.69040']/@energy"));
	System.out.println(operation.evaluate(content));
}
```

The output of this code is:

```
1957.50902
[1957.50902]
```

Note that the first line is the return of the xpath expression, if there are multiple records that have that volume, it will still only return the first hit as a string. If you want the complete list of results, you will have to manually specify the result type.
The second return is a java.util.List of results because the engine has determined that it is possible that there are multiple return values based on the query you performed.

Here you ask for a specific field (note the 0-based of the evaluator vs 1-based of xpath):

```java
public static void main(String...args) throws Exception {
	Document largeDoc = load(new File("/path/to/largedoc.xml"));
	XPath xPath = XPathFactory.newInstance().newXPath();
	XPathExpression expression = xPath.compile("metering[1]/measurement[1]/@energy");
	System.out.println(expression.evaluate(largeDoc.getDocumentElement()));
	XMLContent content = new XMLContent(largeDoc.getDocumentElement());
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("metering[0]/measurement[0]/@energy"));
	System.out.println(operation.evaluate(content));
}
```

The output of this code is:

```
-10954593.87268
-10954593.87268
```

Here you can see the engine knows that the return value is not a compounded result based on a search but a specific query of a value and returns that.
If you want to manually verify that the rule has been parsed correctly, you can always print the operation object, it prints out a hierarchical view of the operation as it will be executed. For example:

```java
public static void main(String...args) throws ParseException { 
	Operation operation = PathAnalyzer.analyze(QueryParser.getInstance().parse(
		"messages[exists(level) && level # allowedLevels & message ~ 'IMPORTANT.*']/message"
	));
	System.out.println(operation);
}
```

Prints:

```
VARIABLE[messages](
	(
		METHOD[public static boolean exists(java.lang.Object)] (
			VARIABLE[level]
		)
	),
	LOGICAL_AND[&&] (
		(
			(
				VARIABLE[level]
			),
			IN[#] (
				VARIABLE[allowedLevels]
			)
		),
		BITWISE_AND[&] (
			(
				VARIABLE[message]
			),
			MATCHES[~],
			STRING[IMPORTANT.*]
		)
	)
),
VARIABLE[/message]
```

## Rule Validation

For validation you can look at this example:

```java
public static void main(String...args) throws ParseException {
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("messages[level < 2]/message"));
	System.out.println(operation.validate(new BeanType(MyMessageProvider.class)));
}

public static interface Message {
	public String getMessage();
	public int getLevel();
}
public static interface MyMessageProvider {
	public List<Message> getMessages();
}

```
As the rule is correct, the evaluator will simply return an empty list of validation messages indicating that everything is ok:

```
[]
```

However suppose we type this:

```java
public static void main(String...args) throws ParseException {
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("messages[logLevel < 2]/message")); System.out.println(operation.validate(new BeanType(MyMessageProvider.class)));
}
```

This is incorrect as the field is not called logLevel, the following is printed:

```
[[] [ERROR] The child logLevel does not exist in the context]
```

Note that this was before actually running any data through the rule, you can validate rules as they are typed/saved. A more complex example:

```java
public static void main(String...args) throws ParseException {
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("messages[exists(level) && level]/message"));
	System.out.println(operation.validate(new BeanType(MyMessageProvider.class)));
}
```

Prints:

```
[[] [ERROR] The operator LOGICAL_AND = && only supports boolean types, the right operand is however of type class java.lang.Integer]
```

The method "exists()" takes any object so it is ok to pass in level but level cannot be converted to a Boolean. Note that method parameters are in fact checked, for example suppose we have this method: 

```java
public static boolean future(java.util.Date date) { 
	return date.after(new java.util.Date());
}
```

And we run:

```java
public static void main(String...args) throws ParseException {
	Operation operation = PathAnalyzer.analyze(
		QueryParser.getInstance().parse("messages[future(level)]/message"));
	System.out.println(operation.validate(new BeanType(MyMessageProvider.class)));
}
```

This will also print out a validation error:

```
[[] [ERROR] Argument 1 expects a class java.util.Date but will instead receive a class java.lang.Integer instance]
```

## Data Binding

A data structure is only as useful as the data you can put in it. To that end there are parsers and formatters for the types API. Currently two types are supported:
- XML: there is transparent support for large XML documents meaning that if you bind an XML to say a structure and tomorrow it turns out that the XML documents can actually be quite large, all you need to do is change a configuration setting.
- Flat: it also supports large flat files using the same method as the XML binding. Fixed length & delimiter based parsing are both supported.

## Conversions

There is an extensible conversion system working in the background which tries to strike a balance between the simplicity of dynamic types with the assurances of static types. You could turn it off altogether or extend it with custom conversion logic, check the convert-api package.
Because of the extra typing information we also allow more complex conversions using this API. 
Where the converter-api package can not convert a string to date due to lack of information, this API actually allows it because the SimpleType date should contain all the information necessary to perform conversions.

## Definition Binding

At some point you need to store your definitions and optionally send them to other parties. There are currently two definition bindings:
- XML: a custom xml format that allows you to store your complex type in a human-readable fashion
- XML Schema: a standards based format to store your complex type in a way that other systems can read it

This means you can take your java class and dump it to an XML schema if you want:

```java
public static void main(String...args) IOException {
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	new XSDDefinitionMarshaller().marshal(output, new BeanType(Message.class));
	System.out.println(new String(output.toByteArray()));
}

@XmlRootElement(name="myMessage")
public static interface Message {
	@Null
	public String getMessage();
	public int getLevel();
}
```

This prints: 

```xml
<schema xmlns="http://www.w3.org/2001/XMLSchema">
	<element name="myMessage">
		<complexType>
			<sequence>
				<element name="message" nillable="true"/>
				<element name="level" type="int"/>
			</sequence>
		</complexType>
	</element>
</schema>
```

## Validation

Apart from the rule validation that the evaluator is capable of, you can also validate data instances against their definitions. 
Because it is all based on the standard API you can do some fun stuff like validate an XML against a java class or a java instance against an XML schema without having to convert them.
The result of a validation is a list of zero or more messages that tell you what is potentially wrong with some data. 
It supports nearly all concepts found in XML schema for example: minOccurs, maxOccurs, minLength, pattern, enumeration,…

Additionally in the case of java beans the validation rules can be expressed using the default java bean validation annotations.
As an example take this working code:

```java
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
...
public static void main(String...args) throws ParserConfigurationException, SAXException, IOException {
	Document xml = load(
		"<logThis>"
		+ " <message>This is a test!</message>"
		+ " <level>10</level>"
		+ "</logThis>"
	);
	XMLContent content = new XMLContent(
		xml.getDocumentElement(),
		new RootElement(new BeanType(Message.class))
	);
	Validator validator = content.getType().createValidator();
	System.out.println(validator.validate(content));
}
public static interface Message {
	@Pattern(regexp="[\\w\\s]+")
	public String getMessage();
	@Max(value = 5)
	public int getLevel();
}
```

As you can see we have a message interface where the level uses default validation constraints to indicate that the value should be less than 5 and the message should match a simple regex.
The XML contains the value 10 for level (in string form I might add) and a message that does not conform to the pattern. The output of this piece of code is:

```
[[message] [ERROR] The string 'This is a test!' does not match the pattern '[\w\s]+', [level] [ERROR] The object is larger than allowed]
```

Removing the exclamation remark in the message and setting level to e.g. 2 will resolve these validation errors.

## Interaction with java code

Suppose you want to design a framework, you don’t want generic interfaces like ComplexContent littering your method parameters because they would lose their descriptive nature.
Using the standard utilities you can do this:

```java
public static void main(String...args) throws ParserConfigurationException, SAXException, IOException {
	Document xml = load(
		"<logThis>"
		+ " <message>This is a test!</message>"
		+ " <level>5</level>"
		+ "</logThis>"
	);
	XMLContent content = new XMLContent(
		xml.getDocumentElement(),
		new RootElement(new BeanType(Message.class))
	);
	log(TypeUtils.getAsBean(content, Message.class));
}
public static void log(Message message) {
	System.out.println("[" + message.getLevel() + "] " + message.getMessage());
}
public static interface Message {
	public String getMessage();
	public int getLevel();
}
```

The important bit here is the "log(Message message)" method which, as you can see, has no dependency to the types API, it simply defines an interface of what it wants in standard bean-notation.
At runtime this could be an actual java class implementing the interface or (as is here the case) an XML or something entirely else, as long as it supports the types API.

The output of this code is as expected:

```
[5] This is a test!
```

## Support for standards

I have tried to use standards as much as possible because not only does it lower the learning curve for developers, it also minimizes the actual dependency on the frameworks in play. In the ideal world the average developer shouldn’t be exposed too much to the internal workings of the data layer, the eventual business logic performed on the data is the important part.
Supported standards:
- Java bean validation API: http://docs.oracle.com/javaee/6/tutorial/doc/gircz.html
- Some of the JAXB annotations are reused, for example you can set element names etc
- XML Schema