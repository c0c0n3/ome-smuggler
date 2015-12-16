package ome.smuggler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.HornetQPersistenceConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.types.ImportConfigReader;
import ome.smuggler.core.types.ImportConfigSource;
import util.config.ConfigProvider;
import util.config.ConfigReader;

/**
 * Spring bean wiring of configuration items.
 */
@Configuration
@Profile(Profiles.Prod)
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
    public ImportConfigSource importConfig(ConfigProvider<ImportConfig> src) {
        return config(ConfigReader.newReader(src, ImportConfigReader::new));
    }
    
    @Bean
    public ImportGcQConfig importGcQConfig(ConfigProvider<ImportGcQConfig> src) {
        return config(src);
    }
    
    @Bean
    public HornetQPersistenceConfig hornetQPersistenceConfig(
            ConfigProvider<HornetQPersistenceConfig> src) {
        return config(src);
    }
    
}
