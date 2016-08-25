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
     * Adds import requests to the import proxy's queue and returns immediately.
     * The proxy will then execute the import asynchronously.
     * @param target the URL of the import proxy where to post the requests.
     * @param request each details an import to run. If some of the requests
     *                have no session key, this service will fill one in by
     *                creating a session with a timeout of {@link
     *                #ImportSessionTimeout}. This session key will then be
     *                shared by all the requests that don't have one.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws RuntimeException if a error occurs while submitting the requests.
     */
    void enqueue(URI target, ImportRequest...request);

}
