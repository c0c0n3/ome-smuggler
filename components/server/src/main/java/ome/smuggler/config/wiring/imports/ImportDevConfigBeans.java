package ome.smuggler.config.wiring.imports;

import ome.smuggler.core.types.OmeCliConfigReader;
import ome.smuggler.core.types.OmeCliConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.config.items.OmeCliConfig;
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
    public OmeCliConfigSource omeCliConfig(ConfigProvider<OmeCliConfig> src) {
        OmeCliConfig cfg = new OmeCliConfig();
        cfg.setOmeCliJarPath("non-existent.jar");  // (*)

        return new OmeCliConfigReader(cfg);
    }
    /* (*) Avoid bombing out on ome-cli jar.
     * If no jar path is configured, the OmeCliConfigReader will try locating an
     * ome-cli jar file in the same directory as Smuggler's jar. This is fine
     * when running the app, but as we test there will be no jar files as the
     * tests are run straight from the compiled classes in the build directory.
     * So we explicitly set a value for the ome-cli jar file which results in
     * the OmeCliConfigReader just returning the value as a path.
     */
    
    @Bean
    public ImportConfigSource importConfig() {
        return new DevImportConfigSource();
    }

}
