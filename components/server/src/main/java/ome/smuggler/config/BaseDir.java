package ome.smuggler.config;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.asOptional;
import static util.string.Strings.isNullOrEmpty;
import static util.string.Strings.requireString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import util.object.Wrapper;

/**
 * Path to a directory used as a base for {@link #resolve(String) resolving} 
 * paths in configuration files.
 * The base directory path is retrieved from the system properties if a property
 * value for a specified key exists; otherwise it will be the current working
 * directory, i.e. the value of "user.dir".
 * Note that if the property value is set to a relative path, then that path
 * will be resolved against the current working directory.
 */
public class BaseDir extends Wrapper<Path> {

    /**
     * Looks up the path associated to the specified key in the system 
     * properties. If no value is found for the key, the current working
     * directory is returned.
     * @param key the system property key.
     * @return the path.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    public static Path lookup(String key) {
        requireString(key, "key");
        
        String dir = asOptional(System.getProperty(key))
                    .orElse(System.getProperty("user.dir"));
        return Paths.get(dir);
    }
    
    /**
     * Sets a system property with the specified key and value.
     * @param key the key.
     * @param dir the value.
     * @throws IllegalArgumentException if the key is {@code null} or empty.
     * @throws NullPointerException if {@code dir} is {@code null}.
     */
    public static void store(String key, Path dir) {
        requireString(key, "key");
        requireNonNull(dir, "dir");
        
        System.setProperty(key, dir.toString());
    }
    
    /**
     * Creates a temporary directory and sets its path in the system properties 
     * using the given key. 
     * @param key the system property key.
     * @param dirNamePrefix name prefix of the temporary directory to create.
     * @return the path to the newly created temporary directory.
     * @throws IOException if the directory could not be created.
     * @throws IllegalArgumentException if any argument is {@code null} or 
     * empty.
     */
    public static Path storeTempDir(String key, String dirNamePrefix) 
            throws IOException {
        requireString(key, "key");
        requireString(dirNamePrefix, "dirNamePrefix");
        
        Path dir = Files.createTempDirectory(dirNamePrefix);
        System.setProperty(key, dir.toString());
        return dir;
    }

    /**
     * Creates a new instance from the system property having the specified key.
     * If no such a property exists, then the current working directory is
     * used instead.
     * @param sysPropKey the system property key.
     * @throws IllegalArgumentException if the key is {@code null} or empty.
     */
    public BaseDir(String sysPropKey) {
        super(lookup(sysPropKey));
    }

    /**
     * Resolves the given path as follows.
     * If the argument is already an absolute path, then it is returned as is;
     * otherwise it is resolved against this base directory. In particular if
     * the path argument is {@code null} or empty, this base directory is
     * returned.
     * @param configuredPath a path in a configuration file.
     * @return the resolved path.
     */
    public Path resolve(String configuredPath) {
        if (isNullOrEmpty(configuredPath)) {
            return get();
        } else {
            return get().resolve(configuredPath);
        }
    }

}
