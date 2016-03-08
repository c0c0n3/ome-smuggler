package ome.smuggler.config;

import static ome.smuggler.config.ConfigItemsWiring.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.data.DefaultHornetQPersistenceConfig;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.config.data.DevMailConfigSource;
import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.HornetQPersistenceConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.MailConfigSource;
import util.config.ConfigProvider;


/**
 * Spring bean wiring of configuration items for the {@link Profiles#Dev Dev}
 * profile.
 */
@Configuration
@Profile(Profiles.Dev)
public class DevConfigItemsWiring {

    public static final String BaseDataDirPropKey = 
            "ome.smuggler.config.BaseDataDirPropKey";
    
    
    private final Path baseDataDir;
    
    public DevConfigItemsWiring() {
        baseDataDir = Paths.get(System.getProperty(BaseDataDirPropKey));
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
    public ImportGcQConfig importGcQConfig(ConfigProvider<ImportGcQConfig> src) {
        return config(src);
    }
    
    @Bean
    public ImportConfigSource importConfig() {
        return new DevImportConfigSource(baseDataDir);
    }
    
    @Bean
    public HornetQPersistenceConfig hornetQPersistenceConfig() {
        return DefaultHornetQPersistenceConfig.build(baseDataDir);
    }
    
    @Bean
    public MailQConfig mailQConfig(ConfigProvider<MailQConfig> src) {
        return config(src);
    }
    
    @Bean
    public MailConfigSource mailConfig() {
        return new DevMailConfigSource(baseDataDir);
    }
    
}
