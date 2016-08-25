package ome.smuggler.jclient.core.service.imports.impl;

import ome.smuggler.jclient.core.config.ComponentsFactory;
import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.imports.ImportRequest;
import ome.smuggler.jclient.core.service.imports.ImportService;
import ome.smuggler.jclient.core.service.omero.SessionService;

import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * Implements the {@link ImportService}.
 */
public class ImportServiceImpl implements ImportService {

    private final SessionService session;

    /**
     * Creates a new instance.
     * @param session the session service.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportServiceImpl(SessionService session) {
        requireNonNull(session, "session");

        this.session = session;
    }

    @Override
    public void enqueue(URI target, ImportRequest...request) {
        requireNonNull(target, "target");
        requireNonNull(request, "request");

        String newSessionKey = null;
        for (ImportRequest r : request) {
            requireNonNull(r, "request");
            if (r.sessionKey == null || r.sessionKey.isEmpty()) {
                if (newSessionKey == null) {
                    newSessionKey = session.create(ImportSessionTimeout);
                }
                r.sessionKey = newSessionKey;
            }
        }
        RestResource<ImportRequest[]> client =
                ComponentsFactory.jsonResource(target);
        client.post(request);
    }

}
