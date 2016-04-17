package ome.smuggler.config;

/**
 * The base directory where to look for configuration files.
 * This path can be specified in the {@link #SysPropKey system properties} at 
 * start up; if not found in the system properties, it will default to the 
 * current working directory of the Smuggler's process.
 * @see ome.smuggler.config.providers.PriorityConfigProvider
 */
public class BaseConfigDir extends BaseDir {

    public static final String SysPropKey = "ome.smuggler.ConfigDir";

    /**
     * Creates a new instance to read the directory value from the system
     * properties, defaulting to the current working directory if no value
     * is found.
     */
    public BaseConfigDir() {
        super(SysPropKey);
    }

}
