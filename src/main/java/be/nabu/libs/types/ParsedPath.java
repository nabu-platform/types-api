package be.nabu.libs.types;

public class ParsedPath {
	
	private String name;
	private ParsedPath childPath;
	private String index;
	
	public ParsedPath(String path) {
		parse(path);
	}
	
	private void parse(String path) {		
		if (path.startsWith("/"))
			throw new IllegalArgumentException("Can not use absolute paths");
		
		int indexOfSlashSeparator = path.indexOf('/');
		int indexOfBracketSeparator = path.indexOf('[');
		if (indexOfSlashSeparator >= 0 && indexOfSlashSeparator > indexOfBracketSeparator) {
			int indexOfClosingBracketSeparator = path.indexOf(']');
			indexOfSlashSeparator = path.indexOf('/', indexOfClosingBracketSeparator);
		}
		// it has a child
		if (indexOfSlashSeparator >= 0) {
			name = path.substring(0, indexOfSlashSeparator);
			childPath = new ParsedPath(path.substring(indexOfSlashSeparator + 1));
		}
		else {
			name = path;
		}
		indexOfBracketSeparator = name.indexOf('[');
		// check if you want indexed access
		if (indexOfBracketSeparator >= 0) {
			// an index reference must end with "]"
			if (!name.endsWith("]"))
				throw new IllegalArgumentException("The path " + path + " contains an indexed field without closing tag: " + name);
			index = name.substring(indexOfBracketSeparator + 1, name.length() - 1).trim();
			// though we don't really need it, it is customary to wrap strings in quotes
			// this allows for familiar expressions
			// disabled currently due to misleading
//			if (index.startsWith("\"") && index.endsWith("\"")) {
//				index = index.substring(1, index.length() - 1);
//			}
			name = name.substring(0, indexOfBracketSeparator);
		}
	}
	public String getName() {
		return name;
	}
	
	public ParsedPath getChildPath() {
		return childPath;
	}
	
	public String getIndex() {
		return index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChildPath(ParsedPath childPath) {
		this.childPath = childPath;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return name + (index == null ? "" : "[" + index + "]") + (childPath == null ? "" : "/" + childPath);
	}
}
