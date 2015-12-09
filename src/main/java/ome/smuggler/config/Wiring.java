package ome.smuggler.config;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.FailedImportHandler;
import ome.smuggler.core.service.ImportLogDisposer;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportEnv;
import ome.smuggler.core.service.impl.ImportFailureHandler;
import ome.smuggler.core.service.impl.ImportLogDeleteAction;
import ome.smuggler.core.service.impl.ImportRunner;
import ome.smuggler.core.service.impl.ImportTrigger;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration.
 */
@Configuration
public class Wiring {
    
    @Bean
    public ImportEnv importEnv(
            ImportConfigSource config, 
            CliImporterConfig cliConfig, 
            ChannelSource<QueuedImport> importSourceChannel,
            SchedulingSource<ImportLogFile> importGcSourceChannel) {
        return new ImportEnv(config, cliConfig, importSourceChannel, 
                             importGcSourceChannel);
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
    public ImportLogDisposer importLogDisposer() {
        return new ImportLogDeleteAction();
    }
    
    @Bean
    public FailedImportHandler failedImportHandler(ImportEnv env) {
        return new ImportFailureHandler(env);
    }
    
}
