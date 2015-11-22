package ome.smuggler.config;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportRunner;
import ome.smuggler.q.DequeueImportTask;
import ome.smuggler.q.EnqueueImportTask;
import ome.smuggler.q.ServerConnector;

import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import util.config.ConfigProvider;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {

    private static <T> T config(ConfigProvider<T> provider) {
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
    public ImportLogConfig importLogConfig(ConfigProvider<ImportLogConfig> src) {
        return config(src);
    }
    
    @Bean
    public ImportRequestor importRequestor(ImportQConfig qConfig, 
            ImportLogConfig logConfig, ServerConnector connector) 
                    throws HornetQException {
        return new EnqueueImportTask(qConfig, logConfig, connector.getSession());
    }
    
    @Bean
    public ImportProcessor importProcessor(CliImporterConfig cliCfg, 
                                           ImportLogConfig logCfg) {
        return new ImportRunner(cliCfg, logCfg);
    }
    
    @Bean
    public DequeueImportTask dequeueImportTask(ImportQConfig config, 
            ServerConnector connector, ImportProcessor processor) 
                    throws HornetQException {
        return new DequeueImportTask(config, connector.getSession(), processor);
    }
    
}
