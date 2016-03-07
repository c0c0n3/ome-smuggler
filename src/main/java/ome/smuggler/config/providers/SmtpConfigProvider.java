package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import util.spring.io.ResourceReader;
import ome.smuggler.config.data.SmtpYmlFile;
import ome.smuggler.config.items.SmtpConfig;


/**
 * Reads mail server configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory; 
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class SmtpConfigProvider extends PriorityConfigProvider<SmtpConfig> {

    public static final String FileName = "smtp.yml";
    
    @Override
    protected ResourceReader<SmtpConfig> getConverter() {
        return new YmlResourceReader<>(SmtpConfig.class);
    }
    
    @Override 
    public Stream<SmtpConfig> getFallback() {
        return new SmtpYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }
    
}
