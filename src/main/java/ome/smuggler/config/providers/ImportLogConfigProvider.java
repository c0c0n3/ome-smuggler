package ome.smuggler.config.providers;

import java.util.stream.Stream;

import ome.smuggler.config.data.ImportLogYmlFile;
import ome.smuggler.config.items.ImportLogConfig;

import org.springframework.stereotype.Component;

import util.spring.io.ResourceReader;

/**
 * Reads import log configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory; 
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class ImportLogConfigProvider 
    extends PriorityConfigProvider<ImportLogConfig> {

    public static final String FileName = "import-log.yml";
    
    @Override
    protected ResourceReader<ImportLogConfig> getConverter() {
        return new YmlResourceReader<>(ImportLogConfig.class);
    }
    
    @Override 
    public Stream<ImportLogConfig> getFallback() {
        return new ImportLogYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }

}
