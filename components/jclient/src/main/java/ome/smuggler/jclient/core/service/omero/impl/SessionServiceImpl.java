package ome.smuggler.jclient.core.service.omero.impl;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import ome.smuggler.jclient.core.service.omero.OmeroException;
import ome.smuggler.jclient.core.service.omero.SessionService;
import omero.ServerError;
import omero.api.ServiceFactoryPrx;
import omero.client;
import omero.model.Session;

import static java.util.Objects.requireNonNull;

/**
 * Implements the {@link SessionService}.
 */
public class SessionServiceImpl implements SessionService {

    private static String group(Session existingSession) {
        return existingSession.getDetails().getGroup().getName().getValue();
    }

    private static long timeToIdle(int timeoutInSeconds) {
        return timeoutInSeconds * 1000L;
    }

    private final ClientFactory factory;

    /**
     * Creates a new instance.
     * @param factory the factory to create OMERO clients.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SessionServiceImpl(ClientFactory factory) {
        requireNonNull(factory, "factory");

        this.factory = factory;
    }

    private String doCreate(int timeout) throws CannotCreateSessionException,
            PermissionDeniedException, ServerError {
        client c = factory.newClient();
        ServiceFactoryPrx serviceFactory = c.createSession();
        serviceFactory.setSecurityPassword(factory.password());

        Session initialSession = serviceFactory.getSessionService()
                .getSession(c.getSessionId());
        Session newSession = serviceFactory.getSessionService()
                .createUserSession(0,
                        timeToIdle(timeout),
                        group(initialSession));
        c.killSession();  // close initial session.

        return newSession.getUuid().getValue();
    }

    @Override
    public String create(int timeout) throws OmeroException {
        try {
            return doCreate(timeout);
        } catch (Exception e) {
            throw new OmeroException(e);
        }
    }

    public static void main(String[] args) {
        ClientFactory cf = new ClientFactory(
                "localhost", 4063, "tasty", "abc123", false);
        SessionService svc = new SessionServiceImpl(cf);
        String id = svc.create(152);
        System.out.println(id);
    }
}
