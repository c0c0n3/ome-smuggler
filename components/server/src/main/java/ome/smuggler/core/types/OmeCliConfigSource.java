package ome.smuggler.core.types;

import java.nio.file.Path;

/**
 * Provides read-only, type-safe access to the OME CLI configuration.
 * @see ome.smuggler.config.items.OmeCliConfig
 */
public interface OmeCliConfigSource {

    /**
     * @return the path to the self-contained OME CLI jar.
     */
    Path omeCliJar();

}
