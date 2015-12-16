package end2end.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ome.smuggler.config.DevConfigItemsWiring;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.core.types.ImportConfigSource;

public class Config {
    
    public final Path baseDataDir;
    public final ImportConfigSource importConfig;
    
    public Config() throws IOException {
        baseDataDir = Files.createTempDirectory("smuggler-tests");
        System.setProperty(DevConfigItemsWiring.BaseDataDirPropKey, 
                           baseDataDir.toString());
        
        importConfig = new DevImportConfigSource(baseDataDir);
        // ...an exact copy of the one used by Spring.
    }
    
}
