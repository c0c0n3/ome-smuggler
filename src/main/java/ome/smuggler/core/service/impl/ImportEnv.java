package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;

/**
 * Provides access to import configuration and queues.
 */
public class ImportEnv {

    private final ImportConfigSource config;
    private final CliImporterConfig cliConfig;
    private final ChannelSource<QueuedImport> queue;
    private final SchedulingSource<ImportLogFile> gcQueue;
    
    public ImportEnv(ImportConfigSource config, CliImporterConfig cliConfig,
            ChannelSource<QueuedImport> queue, 
            SchedulingSource<ImportLogFile> gcQueue) {
        requireNonNull(config, "config");
        requireNonNull(cliConfig, "cliConfig");
        requireNonNull(queue, "queue");
        requireNonNull(gcQueue, "gcQueue");
        
        this.config = config;
        this.cliConfig = cliConfig;
        this.queue = queue;
        this.gcQueue = gcQueue;
    }
    
    public ImportConfigSource config() {
        return config;
    }
    
    public CliImporterConfig cliConfig() {
        return cliConfig;
    }
    
    public ChannelSource<QueuedImport> queue() {
        return queue;
    }
    
    public SchedulingSource<ImportLogFile> gcQueue() {
        return gcQueue;
    }
    
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return new ImportLogPath(config().importLogDir(), taskId);
    }
    
    public ImportLogFile importLogFileFor(ImportId taskId) {
        return new ImportLogFile(importLogPathFor(taskId));
    }
    
}
