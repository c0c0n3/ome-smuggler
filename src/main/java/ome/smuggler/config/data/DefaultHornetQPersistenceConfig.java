package ome.smuggler.config.data;

import java.nio.file.Paths;
import java.util.stream.Stream;

import ome.smuggler.config.items.HornetQPersistenceConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded operational parameters for the embedded HornetQ server.
 */
public class DefaultHornetQPersistenceConfig 
    implements ConfigProvider<HornetQPersistenceConfig> {

    public static final String RootDir = "hornetq-data";
    
    @Override
    public Stream<HornetQPersistenceConfig> readConfig() throws Exception {
        HornetQPersistenceConfig cfg = new HornetQPersistenceConfig();
        cfg.setPersistenceEnabled(true);
        cfg.setBindingsDirPath(Paths.get(RootDir, "bindings").toString());
        cfg.setJournalDirPath(Paths.get(RootDir, "journal").toString());
        cfg.setLargeMessagesDirPath(Paths.get(RootDir, "largemessages").toString());
        cfg.setPagingDirPath(Paths.get(RootDir, "paging").toString());
        
        return Stream.of(cfg);
    }

}
