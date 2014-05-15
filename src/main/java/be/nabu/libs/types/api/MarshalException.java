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
