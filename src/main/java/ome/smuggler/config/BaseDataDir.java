package ome.smuggler.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import util.object.Wrapper;

/**
 * Builds the path to the directory found in the system properties using the
 * {@link #SysPropKey}.
 */
public class BaseDataDir extends Wrapper<Path> {

    public static final String SysPropKey = 
            "ome.smuggler.config.BaseDataDirPropKey";
    
    private static Path getDirFromSysProps() {
        return Paths.get(System.getProperty(SysPropKey));
    }
    
    /**
     * Creates a temporary directory and sets it to be the value of the base
     * data directory in the system properties.
     * @param tempDirName the name of the temporary directory to create.
     * @return the path to newly created directory.
     * @throws IOException if an error occurs while creating the directory.
     */
    public static BaseDataDir setupSysPropToTempDir(String tempDirName)
            throws IOException {
        Path baseDataDir = Files.createTempDirectory(tempDirName);
        System.setProperty(BaseDataDir.SysPropKey, baseDataDir.toString());
        return new BaseDataDir();
    }
    
    /**
     * Creates a new instance reading the directory value from the system
     * properties.
     */
    public BaseDataDir() {
        super(getDirFromSysProps());
    }
    
}
