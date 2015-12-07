package ome.smuggler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.config.items.ImportQConfig;
import util.config.ConfigProvider;

/**
 * Spring bean wiring of configuration items.
 */
@Configuration
public class ConfigItemsWiring {

    public static <T> T config(ConfigProvider<T> provider) {
        return provider.defaultReadConfig().findFirst().get();
    }
    
    @Bean
    public ImportQConfig importQConfig(ConfigProvider<ImportQConfig> src) {
        return config(src);
    }
    
    @Bean
    public CliImporterConfig cliImporterConfig(ConfigProvider<CliImporterConfig> src) {
        return config(src);
    }
    
    @Bean
    public ImportConfig importConfig(ConfigProvider<ImportConfig> src) {
        return config(src);
    }
    
    @Bean
    public ImportGcQConfig importGcQConfig(ConfigProvider<ImportGcQConfig> src) {
        return config(src);
    }
    
}
