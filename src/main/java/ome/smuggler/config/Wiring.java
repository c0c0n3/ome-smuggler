package ome.smuggler.config;

import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.q.EnqueueImportTask;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {

    @Bean
    public ImportRequestor importRequestor() {
        return new EnqueueImportTask();
    }
    
}
