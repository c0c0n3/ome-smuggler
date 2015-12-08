package ome.smuggler.config;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.ImportLogDisposer;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportEnv;
import ome.smuggler.core.service.impl.ImportLogDeleteAction;
import ome.smuggler.core.service.impl.ImportRunner;
import ome.smuggler.core.service.impl.ImportTrigger;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.q.DequeueTask;
import ome.smuggler.q.EnqueueTask;
import ome.smuggler.q.QueueConnector;
import ome.smuggler.q.ScheduleTask;
import ome.smuggler.q.ServerConnector;

import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {
    
    private ChannelSource<QueuedImport> importSourceChannel(
            ServerConnector connector, ImportQConfig qConfig) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(qConfig, connector.getSession());
        return new EnqueueTask<QueuedImport>(q).asDataSource();
    }
    
    private SchedulingSource<ImportLogFile> importGcSourceChannel(
            ServerConnector connector, ImportGcQConfig qConfig) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(qConfig, connector.getSession());
        return new ScheduleTask<>(q);
    }
    
    @Bean
    public ImportEnv importEnv(ServerConnector connector, ImportQConfig qConfig, 
            ImportGcQConfig gcQConfig, ImportConfigSource config, 
            CliImporterConfig cliConfig) throws HornetQException {
        return new ImportEnv(config, 
                             cliConfig, 
                             importSourceChannel(connector, qConfig), 
                             importGcSourceChannel(connector, gcQConfig));
    }
    
    @Bean
    public ImportRequestor importRequestor(ImportEnv env) {
        return new ImportTrigger(env);
    }
    
    @Bean
    public ImportProcessor importProcessor(ImportEnv env) {
        return new ImportRunner(env);
    }
    
    @Bean
    public DequeueTask<QueuedImport> dequeueImportTask(ImportQConfig config, 
            ServerConnector connector, ImportProcessor processor) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(config, connector.getSession());
        return new DequeueTask<>(q, processor::consume, QueuedImport.class);
    }
    
    @Bean
    public DequeueTask<ImportLogFile> dequeueImportLogDisposalTask(
            ImportGcQConfig config, ServerConnector connector) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(config, connector.getSession());
        ImportLogDisposer reaper = new ImportLogDeleteAction();
        return new DequeueTask<>(q, reaper::dispose, ImportLogFile.class);
    }
    
}
