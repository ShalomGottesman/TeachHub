package authentication;

public class InvalidCredentialException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5617028363950345372L;

	public InvalidCredentialException(String msg) {
		super(msg);
	}
}
