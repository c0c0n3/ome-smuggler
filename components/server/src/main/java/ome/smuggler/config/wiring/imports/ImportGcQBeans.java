package ome.smuggler.config.wiring.imports;

import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.imports.ImportLogDisposer;
import ome.smuggler.core.types.ImportLogFile;
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
    public QChannelFactory<ImportLogFile> importGcChannelFactory(
            ServerConnector connector, ImportGcQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public SchedulingSource<ImportLogFile> importGcSourceChannel(
            QChannelFactory<ImportLogFile> factory) throws HornetQException {
        return factory.buildSchedulingSource();
    }
    
    @Bean
    public DequeueTask<ImportLogFile> dequeueImportLogDisposalTask(
            QChannelFactory<ImportLogFile> factory,
            ImportLogDisposer reaper) throws HornetQException {
        return factory.buildSink(reaper::dispose, ImportLogFile.class);
    }

}
