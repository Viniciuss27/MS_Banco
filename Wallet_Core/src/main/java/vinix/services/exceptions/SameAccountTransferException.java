package vinix.services.exceptions;

public class SameAccountTransferException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SameAccountTransferException(String message) {
        super(message);
    }
}