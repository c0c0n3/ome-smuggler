package ome.smuggler.config;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.ImportLogDisposer;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportLogDeleteAction;
import ome.smuggler.core.service.impl.ImportRunner;
import ome.smuggler.core.service.impl.ImportTrigger;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.q.DequeueTask;
import ome.smuggler.q.EnqueueTask;
import ome.smuggler.q.QueueConnector;
import ome.smuggler.q.ScheduleTask;
import ome.smuggler.q.ServerConnector;

import java.time.Duration;

import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import util.config.ConfigProvider;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {

    public static <T> T config(ConfigProvider<T> provider) {
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
    public ImportGcQConfig importGcQConfig(ConfigProvider<ImportGcQConfig> src) {
        return config(src);
    }
    
    @Bean
    public ImportRequestor importRequestor(ImportQConfig qConfig, 
            ImportLogConfig logConfig, ServerConnector connector) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(qConfig, connector.getSession());
        ChannelSource<QueuedImport> channel = new EnqueueTask<>(q);
        return new ImportTrigger(channel, logConfig);
    }
    
    @Bean
    public ChannelSource<ImportLogFile> importGcQCSourceChannel(
            ImportGcQConfig qConfig, ServerConnector connector,
            ImportLogConfig logCfg) 
                    throws HornetQException {
        QueueConnector q = new QueueConnector(qConfig, connector.getSession());
        long span = logCfg.getRetentionMinutes();
        return new ScheduleTask<>(q, Duration.ofMinutes(span));
    }
    
    @Bean
    public ImportProcessor importProcessor(CliImporterConfig cliCfg, 
            ImportLogConfig logCfg, ChannelSource<ImportLogFile> gcQueue) {
        return new ImportRunner(cliCfg, logCfg, gcQueue);
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
