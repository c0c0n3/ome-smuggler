package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.OmeCliConfigSource;

/**
 * Provides access to service configuration and auxiliary services such as
 * logging.
 */
public class OmeroEnv {

    private final OmeCliConfigSource config;
    private final OmeroLogger log;

    /**
     * Creates a new instance.
     * @param config provides access the the configuration values.
     * @param log the log service.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public OmeroEnv(OmeCliConfigSource config, LogService log) {
        requireNonNull(config, "config");
        requireNonNull(log, "log");

        this.config = config;
        this.log = new OmeroLogger(log);
    }

    /**
     * @return the configuration values.
     */
    public OmeCliConfigSource config() {
        return config;
    }

    /**
     * @return the log service.
     */
    public OmeroLogger log() {
        return log;
    }

}
