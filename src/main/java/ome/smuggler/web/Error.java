package ome.smuggler.web;

import static util.object.Either.left;
import util.object.Either;

/**
 * Web clients receive instances of this class if their request results in an
 * error.
 * This is just a data transfer object whose sole purpose is to facilitate the
 * transfer of information from the client to the server.
 */
public class Error {
    
    /**
     * Utility method to create a left {@link Either} with an {@link Error}.
     * @param reason the reason to set in the returned error.
     * @return a left {@link Either} with an {@link Error} having the specified
     * reason.
     */
    public static <R> Either<Error, R> error(String reason) {
        Error e = new Error();
        e.reason = reason;
        return left(e);
    }
    

    /**
     * Details what went wrong.
     */
    public String reason;
    
}
