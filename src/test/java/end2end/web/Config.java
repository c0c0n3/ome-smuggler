package end2end.web;

import java.io.IOException;
import java.nio.file.Path;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.core.types.ImportConfigSource;

public class Config {
    
    public final Path baseDataDir;
    public final ImportConfigSource importConfig;
    
    public Config() throws IOException {
        baseDataDir = BaseDataDir.setupSysPropToTempDir("smuggler-tests").get();
        
        importConfig = new DevImportConfigSource(baseDataDir);
        // ...an exact copy of the one used by Spring.
    }
    
}
