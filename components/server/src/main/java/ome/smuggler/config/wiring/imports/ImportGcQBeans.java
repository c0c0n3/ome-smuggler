package ome.smuggler.config.wiring.imports;

import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.imports.ImportDisposer;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportGcQBeans {

    @Bean
    public QChannelFactory<ProcessedImport> importGcChannelFactory(
            ServerConnector connector, ImportGcQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public SchedulingSource<ProcessedImport> importGcSourceChannel(
            QChannelFactory<ProcessedImport> factory) throws HornetQException {
        return factory.buildSchedulingSource();
    }
    
    @Bean
    public DequeueTask<ProcessedImport> dequeueImportLogDisposalTask(
            QChannelFactory<ProcessedImport> factory,
            ImportDisposer reaper) throws HornetQException {
        return factory.buildSink(reaper::dispose, ProcessedImport.class);
    }

}
