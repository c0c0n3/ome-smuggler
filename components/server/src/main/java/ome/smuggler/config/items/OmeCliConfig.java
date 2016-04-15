package ome.smuggler.config.items;

/**
 * Holds the data needed to call the various OMERO CLI commands.
 * This configuration is used internally and doesn't need to be exposed to
 * system administrators. In fact we use this class to configure the path to
 * {@link #setOmeCliJarPath(String) OME CLI (self-contained) jar file}. If not
 * specified, then the jar is assumed to be located in the same directory as
 * Smuggler's jar and have a name starting with the {@link #setOmeCliJarPrefix(String)
 * configured prefix} unless such prefix is {@code null} or empty, in which case
 * it defaults to {@link #DefaultOmeCliJarPrefix}.</li>
 * </ul>
 */
public class OmeCliConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    public static final String DefaultOmeCliJarPrefix = "ome-cli";

    private String omeCliJarPath;
    private String omeCliJarPrefix;


    public String getOmeCliJarPath() {
        return omeCliJarPath;
    }

    public void setOmeCliJarPath(String omeCliJarPath) {
        this.omeCliJarPath = omeCliJarPath;
    }

    public String getOmeCliJarPrefix() {
        return omeCliJarPrefix;
    }

    public void setOmeCliJarPrefix(String omeCliJarPrefix) {
        this.omeCliJarPrefix = omeCliJarPrefix;
    }

}
