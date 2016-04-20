package ome.smuggler.core.service.omero;

import java.net.URI;

/**
 * Provides access to OMERO sessions functionality.
 */
public interface SessionService {

    /**
     * Issues a keep-alive command for the specified session.
     * @param omeroHostAndPort detail the server to connect to.
     * @param sessionKey the session ID.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     */
    boolean keepAlive(URI omeroHostAndPort, String sessionKey);

    /**
     * Closes the specified session.
     * @param omeroHostAndPort detail the server to connect to.
     * @param sessionKey the session ID.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     */
    boolean close(URI omeroHostAndPort, String sessionKey);

}
