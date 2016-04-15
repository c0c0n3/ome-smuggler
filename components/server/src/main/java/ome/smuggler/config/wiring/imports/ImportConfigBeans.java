package ome.smuggler.config.wiring.imports;

import ome.smuggler.core.types.OmeCliConfigReader;
import ome.smuggler.core.types.OmeCliConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportKeepAliveQConfig;
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
public class ImportConfigBeans {

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
    public OmeCliConfigSource omeCliConfig(ConfigProvider<OmeCliConfig> src) {
        return ConfigReader.newReader(src, OmeCliConfigReader::new).first();
    }
    
    @Bean
    public ImportConfigSource importConfig(ConfigProvider<ImportConfig> src) {
        return ConfigReader.newReader(src, ImportConfigReader::new).first();
    }

}
