package app.exceptions;

public class NoPacksAvailableException extends Exception {

    // Constructors
    public NoPacksAvailableException() {
        super();
    }

    public NoPacksAvailableException(String message) {
        super(message);
    }

    public NoPacksAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPacksAvailableException(Throwable cause) {
        super(cause);
    }

}
