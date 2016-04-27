package ome.smuggler.jclient.core.config;

import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.omero.SessionService;
import ome.smuggler.jclient.core.service.omero.impl.ClientFactory;
import ome.smuggler.jclient.core.service.omero.impl.SessionServiceImpl;
import ome.smuggler.jclient.providers.http.JsonRestResourceAdapter;

import java.net.URI;

/**
 * Binds component implementations to interfaces so we can code against
 * interfaces rather than depending on actual implementations.
 */
public class ComponentsFactory {

    public static <T> RestResource<T> jsonResource(URI target) {
        return new JsonRestResourceAdapter<>(target);
    }

    public static SessionService omeroSession(String host, int port,
        String username, String password) {
        ClientFactory cf = new ClientFactory(host, port, username, password);
        return new SessionServiceImpl(cf);
    }

    public static SessionService omeroSession(String host, int port,
        String username, String password, boolean secure) {
        ClientFactory cf = new ClientFactory(host, port, username, password,
                secure);
        return new SessionServiceImpl(cf);
    }

}
