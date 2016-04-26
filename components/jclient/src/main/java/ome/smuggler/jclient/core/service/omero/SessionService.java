package ome.smuggler.jclient.core.service.omero;

/**
 * Provides access to OMERO sessions functionality.
 */
public interface SessionService {

    /**
     * Creates a new session with the specified timeout.
     * @param timeout the number of seconds of inactivity after which OMERO will
     *                automatically close the session.
     * @return the ID of the newly created session.
     * @throws OmeroException if an error occurs.
     */
    String create(int timeout) throws OmeroException;

}
