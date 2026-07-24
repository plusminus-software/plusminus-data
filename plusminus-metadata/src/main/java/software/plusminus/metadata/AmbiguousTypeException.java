package software.plusminus.metadata;

/**
 * Thrown when a simple class name maps to more than one registered class.
 *
 * <p>Extends {@link IllegalStateException} for backward compatibility, but callers at the web
 * boundary are expected to translate it into a client error (HTTP 4xx) rather than a 500,
 * because the offending value comes from the client request.
 */
public class AmbiguousTypeException extends IllegalStateException {

    public AmbiguousTypeException(String message) {
        super(message);
    }
}
