package ome.smuggler.config.items;

/**
 * Holds the data needed to configure a run of the OMERO CLI importer.
 */
public class CliImporterConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private String mainClassFqn;
    private String omeLibDirPath;
    
    public String getMainClassFqn() {
        return mainClassFqn;
    }
    
    public void setMainClassFqn(String mainClassFqn) {
        this.mainClassFqn = mainClassFqn;
    }
    
    public String getOmeLibDirPath() {
        return omeLibDirPath;
    }
    
    public void setOmeLibDirPath(String omeLibDirPath) {
        this.omeLibDirPath = omeLibDirPath;
    }
    
}
