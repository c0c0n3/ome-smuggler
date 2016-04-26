package ome.smuggler.jclient.core.service.omero.impl;


import omero.client;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Creates pre-configured instances of OMERO clients.
 */
public class ClientFactory {

    private final String host;
    private final int port;
    private final boolean secure;
    private final String username;
    private final String password;

    /**
     * Creates a new factory to deliver OMERO clients using a secure connection.
     * @param host the host where the OMERO server is running.
     * @param port the port the OMERO server is listening to.
     * @param username the username to use for logging into OMERO.
     * @param password the password to use for logging into OMERO.
     * @throws NullPointerException if the {@code host} is {@code null}.
     */
    public ClientFactory(String host, int port, String username,
                         String password) {
        this(host, port, username, password, true);
    }

    /**
     * Creates a new factory to deliver OMERO clients.
     * @param host the host where the OMERO server is running.
     * @param port the port the OMERO server is listening to.
     * @param username the username to use for logging into OMERO.
     * @param password the password to use for logging into OMERO.
     * @param secure whether clients created by this factory will use a secure
     *               connection to OMERO.
     * @throws NullPointerException if the {@code host} is {@code null}.
     */
    public ClientFactory(String host, int port, String username,
                         String password, boolean secure) {
        requireNonNull(host, "host");

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.secure = secure;
    }

    private Map<String, String> buildRequestContext() {
        Map<String, String> props = new HashMap<>();

        props.put("omero.host", host);
        props.put("omero.port", String.valueOf(port));
        if (!secure) {
            props.put(
                "Ice.Default.Router",
                "OMERO.Glacier2/router:tcp -p @omero.port@ -h @omero.host@");
        }

        return props;
    }

    /**
     * Instantiates a new OMERO client.
     * @return the new client.
     */
    public client newClient() {
        Map<String, String> props = buildRequestContext();

        props.put("omero.user", String.valueOf(username));
        props.put("omero.pass", String.valueOf(password));

        return new client(props);
    }

    /**
     * @return the username used for logging into OMERO.
     */
    public String username() {
        return username;
    }

    /**
     * @return the password used for logging into OMERO.
     */
    public String password() {
        return password;
    }

}
