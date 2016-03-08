package ome.smuggler.config;

import javax.jms.ConnectionFactory;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulableFactory;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportLogDisposer;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.q.DequeueTask;
import ome.smuggler.q.QChannelFactory;
import ome.smuggler.q.ServerConnector;


/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class HornetQWiring {
    
    @Bean
    public ServerConnector hornetQServerConnector(ConnectionFactory cf) 
            throws Exception {
        HornetQConnectionFactory factory = (HornetQConnectionFactory) cf; 
        ServerLocator locator = factory.getServerLocator();
        return new ServerConnector(locator);
    }
    
    @Bean
    public QChannelFactory<QueuedImport> importChannelFactory(
            ServerConnector connector, ImportQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public QChannelFactory<ImportLogFile> importGcChannelFactory(
            ServerConnector connector, ImportGcQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedImport> importSourceChannel(
            QChannelFactory<QueuedImport> factory) throws HornetQException {
        return factory.buildSource();
    }
    
    @Bean
    public SchedulingSource<ImportLogFile> importGcSourceChannel(
            QChannelFactory<ImportLogFile> factory) throws HornetQException {
        return factory.buildSchedulingSource();
    }

    @Bean
    public DequeueTask<QueuedImport> dequeueImportTask(
            QChannelFactory<QueuedImport> factory,
            ImportConfigSource importConfig,
            ImportProcessor processor,
            FailedImportHandler failureHandler) throws HornetQException {
        Reschedulable<QueuedImport> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        importConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer, QueuedImport.class);
    }
    
    @Bean
    public DequeueTask<ImportLogFile> dequeueImportLogDisposalTask(
            QChannelFactory<ImportLogFile> factory,
            ImportLogDisposer reaper) throws HornetQException {
        return factory.buildSink(reaper::dispose, ImportLogFile.class);
    }
    
}
