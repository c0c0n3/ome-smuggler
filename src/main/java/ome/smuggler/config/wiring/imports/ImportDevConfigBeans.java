package ome.smuggler.config.wiring.imports;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.Profiles;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportKeepAliveQConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.types.ImportConfigSource;
import util.config.ConfigProvider;

/**
 * Spring bean wiring of configuration items for the {@link Profiles#Dev Dev}
 * profile.
 */
@Configuration
@Profile(Profiles.Dev)
public class ImportDevConfigBeans {

    @Bean
    public ImportQConfig importQConfig(ConfigProvider<ImportQConfig> src) {
        return src.first();
    }
    
    @Bean
    public ImportGcQConfig importGcQConfig(ConfigProvider<ImportGcQConfig> src) {
        return src.first();
    }
    
    @Bean
    public ImportKeepAliveQConfig importKeepAliveQConfig(
            ConfigProvider<ImportKeepAliveQConfig> src) {
        return src.first();
    }
    
    @Bean
    public CliImporterConfig cliImporterConfig(ConfigProvider<CliImporterConfig> src) {
        return src.first();
    }
    
    @Bean
    public ImportConfigSource importConfig() {
        return new DevImportConfigSource(new BaseDataDir().get());
    }
    
}
