package ome.smuggler.config.wiring.imports;

import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ImportKeepAliveQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused.
 */
@Configuration
public class ImportKeepAliveQBeans {

    @Bean
    public QChannelFactory<ImportKeepAlive> importKeepAliveChannelFactory(
            ServerConnector connector, ImportKeepAliveQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<ImportKeepAlive> importKeepAliveSourceChannel(
            QChannelFactory<ImportKeepAlive> factory) throws HornetQException {
        return factory.buildCountedScheduleSource().asDataSource();
    }
    
    @Bean
    public DequeueTask<ImportKeepAlive> dequeueImportKeepAliveTask(
            QChannelFactory<ImportKeepAlive> factory,
            ImportTracker tracker) throws HornetQException {
        return factory.buildReschedulableSink(tracker, ImportKeepAlive.class);
    }
    
}
