package ome.smuggler.jclient.core.service.imports;

import java.net.URI;

/**
 * Offloads an OMERO import to an import proxy.
 */
public interface ImportService {

    /**
     * Idle timeout, in seconds, to use for an import session.
     * Currently set to one week.
     */
    int ImportSessionTimeout = 86400 * 7;

    /**
     * Adds an import request to the import proxy's queue and returns
     * immediately. The proxy will then execute the import asynchronously.
     * @param target the URL of the import proxy where to post the request.
     * @param request details the import to run; if no session key is specified,
     *                this service will fill one in by creating a session with
     *                a timeout of {@link #ImportSessionTimeout}.
     * @return the session key that will be used for the import. If the request
     * specifies one, than that one will be returned; otherwise the one of the
     * session this service created for the import. This is useful if the
     * session needs to be reused for a subsequent import.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws RuntimeException if a error occurs while performing the service
     * action.
     */
    String enqueue(URI target, ImportRequest request);

}
