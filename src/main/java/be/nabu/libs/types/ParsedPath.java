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

package be.nabu.libs.types;

import java.util.HashMap;
import java.util.Map;

public class ParsedPath {
	
	private String name;
	private ParsedPath childPath;
	private String index;
	
	private static Map<String, ParsedPath> paths = new HashMap<String, ParsedPath>();
	
	public static ParsedPath parse(String path) {
		if (!paths.containsKey(path)) {
			synchronized(paths) {
				if (!paths.containsKey(path)) {
					paths.put(path, new ParsedPath(path));
				}
			}
		}
		return paths.get(path);
	}
	
	public ParsedPath(String path) {
		parsePath(path);
	}
	
	private void parsePath(String path) {		
		if (path.startsWith("/"))
			path = path.substring(1);
		
		int indexOfSlashSeparator = -1;
		int depth = 0;
		int indexOfBracketSeparator = -1;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/' && depth == 0) {
				indexOfSlashSeparator = i;
				break;
			}
			else if (path.charAt(i) == '[') {
				if (depth == 0) {
					indexOfBracketSeparator = i;
				}
				depth++;
			}
			else if (path.charAt(i) == ']') {
				depth--;
			}
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
