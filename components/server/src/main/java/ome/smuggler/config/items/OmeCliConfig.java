package ome.smuggler.config.items;

/**
 * Holds the data needed to call the various OMERO CLI commands.
 */
public class OmeCliConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private String omeLibDirPath;
    private String importerMainClassFqn;
    private String keepAliveMainClassFqn;
    
    public String getOmeLibDirPath() {
        return omeLibDirPath;
    }
    
    public void setOmeLibDirPath(String omeLibDirPath) {
        this.omeLibDirPath = omeLibDirPath;
    }
    
    public String getImporterMainClassFqn() {
        return importerMainClassFqn;
    }
    
    public void setImporterMainClassFqn(String mainClassFqn) {
        this.importerMainClassFqn = mainClassFqn;
    }

    public String getKeepAliveMainClassFqn() {
        return keepAliveMainClassFqn;
    }

    public void setKeepAliveMainClassFqn(String keepAliveMainClassFqn) {
        this.keepAliveMainClassFqn = keepAliveMainClassFqn;
    }
    
}
