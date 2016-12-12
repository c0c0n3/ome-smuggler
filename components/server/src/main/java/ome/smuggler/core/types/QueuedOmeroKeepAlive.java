package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.net.URI;
import java.util.Objects;

/**
 * Represents an OMERO session keep-alive request that has been queued and is
 * waiting to be serviced.
 */
public class QueuedOmeroKeepAlive {

    private final URI omero;
    private final String sessionKey;


    /**
     * Creates a new instance.
     * @param omero the host and port of the OMERO server holding the session
     *              to keep alive.
     * @param sessionKey the ID of the OMERO session to keep alive.
     * @throws NullPointerException if the OMERO argument is {@code null}.
     * @throws IllegalArgumentException if the session key argument is {@code
     * null} or empty.
     */
    public QueuedOmeroKeepAlive(URI omero, String sessionKey) {
        requireNonNull(omero, "omero");
        requireString(sessionKey, "sessionKey");

        this.omero = omero;
        this.sessionKey = sessionKey;
    }

    /**
     * @return the host and port of the OMERO server holding the session to
     * keep alive.
     */
    public URI getOmero() {
        return omero;
    }

    /**
     * @return the ID of the OMERO session to keep alive.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof QueuedOmeroKeepAlive) {
            QueuedOmeroKeepAlive other = (QueuedOmeroKeepAlive) x;
            return Objects.equals(omero, other.omero)
                && Objects.equals(sessionKey, other.sessionKey);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(omero, sessionKey);
    }

}
