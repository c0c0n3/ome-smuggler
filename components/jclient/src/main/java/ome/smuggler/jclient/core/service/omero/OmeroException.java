package ome.smuggler.jclient.core.service.omero;

/**
 * Wraps any error occurred while interacting with OMERO into a runtime
 * exception.
 */
public class OmeroException extends RuntimeException {

    /**
     * Wraps the given exception and copies its error message into this
     * exception's own message.
     * @param cause the cause of the error.
     */
    public OmeroException(Exception cause) {
        super(cause.getMessage(), cause);
    }

}
