package ome.smuggler.core.types;

import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.io.FileOps;
import util.runtime.jvm.ClassPathLocator;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.isNullOrEmpty;

/**
 * Implements {@link OmeCliConfigSource} by reading configuration values as
 * specified by {@link OmeCliConfig} and provide type-safe access to said
 * values.
 */
public class OmeCliConfigReader implements OmeCliConfigSource {

    private final Path omeCliJar;

    /**
     * Creates a new instance to read the given configuration.
     * @param config the raw configuration values.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public OmeCliConfigReader(OmeCliConfig config) {
        requireNonNull(config, "config");

        omeCliJar = jarPath(config);
    }

    private Path jarPath(OmeCliConfig config) {
        String rawPath = config.getOmeCliJarPath();
        if (isNullOrEmpty(rawPath)) {
            return defaultJarPath(config.getOmeCliJarPrefix());
        } else {
            return Paths.get(rawPath);
        }
    }

    private Path defaultJarPath(String omeCliJarPrefix) {
        String prefix = isNullOrEmpty(omeCliJarPrefix) ?
                        OmeCliConfig.DefaultOmeCliJarPrefix :
                        omeCliJarPrefix;
        return ClassPathLocator.findBase(getClass())
                               .map(p -> find(p, prefix))
                               .orElseThrow(() -> new RuntimeException("cannot locate main jar"));
    }

    private Path find(Path thisJar, String prefix) {
        return FileOps.listChildFiles(thisJar.getParent())
                      .filter(p -> p.startsWith(prefix) && p.endsWith("jar"))
                      .findFirst()
                      .orElseThrow(() -> new RuntimeException("cannot locate OME CLI jar: " + prefix));
    }

    @Override
    public Path omeCliJar() {
        return omeCliJar;
    }

}
