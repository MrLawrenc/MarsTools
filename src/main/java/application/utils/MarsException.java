package application.utils;

public class MarsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1184374488073806126L;

	public MarsException(String string) {
		super(string);
	}

	public MarsException(String string, Throwable throwable) {
		super(string, throwable);
	}

}
