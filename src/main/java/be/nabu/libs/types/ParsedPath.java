package be.nabu.libs.types;

public class ParsedPath {
	
	private String name, childPath;
	private Integer index;
	
	public ParsedPath(String path) {
		parse(path);
	}
	
	private void parse(String path) {		
		if (path.startsWith("/"))
			throw new IllegalArgumentException("Can not use absolute paths");
		
		int indexOfSeparator = path.indexOf('/');
		// it has a child
		if (indexOfSeparator >= 0) {
			name = path.substring(0, indexOfSeparator);
			childPath = path.substring(indexOfSeparator + 1);
		}
		else
			name = path;
		// check if you want indexed access
		indexOfSeparator = name.indexOf('[');
		if (indexOfSeparator >= 0) {
			// an index reference must end with "]"
			if (!name.endsWith("]"))
				throw new IllegalArgumentException("The path " + path + " contains an indexed field without closing tag");
			index = new Integer(name.substring(indexOfSeparator + 1, name.length() - 1));
			name = name.substring(0, indexOfSeparator);
		}
	}
	public String getName() {
		return name;
	}
	
	public ParsedPath getChildPath() {
		return childPath == null ? null : new ParsedPath(childPath);
	}
	
	public Integer getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return name + (index == null ? "" : "[" + index + "]") + (childPath == null ? "" : "/" + childPath);
	}
}
