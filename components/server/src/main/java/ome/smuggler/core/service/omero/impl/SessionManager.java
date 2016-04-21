package ome.smuggler.core.service.omero.impl;

import ome.smuggler.core.service.omero.SessionService;

import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * Implements the {@link SessionService}.
 */
public class SessionManager implements SessionService {

    private final OmeroEnv env;

    /**
     * Creates a new instance.
     * @param env the service environment.
     */
    public SessionManager(OmeroEnv env) {
        requireNonNull(env, "env");

        this.env = env;
    }

    @Override
    public boolean keepAlive(URI omeroHostAndPort, String sessionKey) {
        KeepAliveCommandBuilder cmd =
                new KeepAliveCommandBuilder(env.config(),
                        omeroHostAndPort, sessionKey);
        OmeCliCommandRunner runner = new OmeCliCommandRunner(env, cmd);
        return runner.run();
    }

    @Override
    public boolean close(URI omeroHostAndPort, String sessionKey) {
        CloseCommandBuilder cmd = new CloseCommandBuilder(env.config(),
                        omeroHostAndPort, sessionKey);
        OmeCliCommandRunner runner = new OmeCliCommandRunner(env, cmd);
        return runner.run();
    }

}
