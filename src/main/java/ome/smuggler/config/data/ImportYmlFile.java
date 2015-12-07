package ome.smuggler.config.data;

import java.time.Duration;
import java.util.stream.Stream;

import ome.smuggler.config.items.ImportConfig;
import util.config.ConfigProvider;


/**
 * Default configuration for the import, i.e. the content of the YAML file
 * if provided.
 */
public class ImportYmlFile implements ConfigProvider<ImportConfig> {

    @Override
    public Stream<ImportConfig> readConfig() {
        ImportConfig cfg = new ImportConfig();
        cfg.setImportLogDir("import/log");
        cfg.setFailedImportLogDir("import/failed-log");
        cfg.setLogRetentionMinutes(Duration.ofDays(15).toMinutes());
        cfg.setRetryIntervals(new Long[] { 10L, 10L, 120L, 1440L });
        
        return Stream.of(cfg);
    }
    
}
