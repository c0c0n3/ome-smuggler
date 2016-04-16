package ome.smuggler.config;


/**
 * The base directory where we keep all the import, mail, and HornetQ data.
 */
public class BaseDataDir extends BaseDir {

    public static final String SysPropKey = 
            "ome.smuggler.config.BaseDataDirPropKey";

    
    /**
     * Creates a new instance reading the directory value from the system
     * properties.
     */
    public BaseDataDir() {
        super(SysPropKey);
    }
    
}
