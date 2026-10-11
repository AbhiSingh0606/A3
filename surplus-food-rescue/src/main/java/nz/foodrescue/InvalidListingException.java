package nz.foodrescue;

/** Thrown when listing details break a validation rule. */
public class InvalidListingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Creates the exception with a message. */
    public InvalidListingException(String message) {
        super(message);
    }
}