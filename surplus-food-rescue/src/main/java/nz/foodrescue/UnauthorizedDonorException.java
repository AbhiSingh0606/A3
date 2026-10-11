package nz.foodrescue;

/** Thrown when a donor who is not verified tries to create a listing. */
public class UnauthorizedDonorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Creates the exception with a message. */
    public UnauthorizedDonorException(String message) {
        super(message);
    }
}
