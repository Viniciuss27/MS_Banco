package vinix.resources.exceptions;

public class DuplicateDocumentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

 public DuplicateDocumentException(String message) {
     super(message);
 }
}
