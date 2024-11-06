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

public class MarshalException extends RuntimeException {

	private static final long serialVersionUID = 3097866132242814715L;

	public MarshalException(String message) {
		super(message);
	}
	
	public MarshalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MarshalException(Throwable cause) {
		super(cause);
	}
}
