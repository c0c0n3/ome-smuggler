package ome.smuggler.config;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportRunner;
import ome.smuggler.q.DequeueImportTask;
import ome.smuggler.q.EnqueueImportTask;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import util.config.ConfigProvider;

/**
 * Spring bean wiring configuration.
 */
@Configuration
@Profile(Profiles.Prod)
public class Wiring {

    @Bean
    public ImportQConfig importQConfig(ConfigProvider<ImportQConfig> provider) {
        return provider.defaultReadConfig().findFirst().get();
    }
    
    @Bean
    public ImportRequestor importRequestor(ImportQConfig config, 
            ClientSession session) throws HornetQException {
        return new EnqueueImportTask(config, session);
    }
    
    @Bean
    public ImportProcessor importProcessor() {
        return new ImportRunner();
    }
    
    @Bean
    public DequeueImportTask dequeueImportTask(ImportQConfig config, 
            ClientSession session, ImportProcessor processor) 
                    throws HornetQException {
        return new DequeueImportTask(config, session, processor);
    }
    
}
