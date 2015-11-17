package ome.smuggler.config.data;

import java.time.Duration;
import java.util.stream.Stream;

import ome.smuggler.config.items.ImportLogConfig;
import util.config.ConfigProvider;


/**
 * Default configuration for the import logs, i.e. the content of the YAML file
 * if provided.
 */
public class ImportLogYmlFile implements ConfigProvider<ImportLogConfig> {

    @Override
    public Stream<ImportLogConfig> readConfig() {
        ImportLogConfig cfg = new ImportLogConfig();
        cfg.setImportLogDir("import-logs");
        cfg.setRetentionMinutes(Duration.ofDays(15).toMinutes());
        
        return Stream.of(cfg);
    }
    
}
