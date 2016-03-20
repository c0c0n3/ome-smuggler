package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import java.nio.file.Path;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.types.FutureTimepoint;
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
    private final TaskFileStore<ImportId> failedImportLogStore;
    private final MailRequestor mail;
    private final ImportLogger log;
    
    public ImportEnv(ImportConfigSource config, CliImporterConfig cliConfig,
            ChannelSource<QueuedImport> queue, 
            SchedulingSource<ImportLogFile> gcQueue,
            TaskFileStore<ImportId> failedImportLogStore,
            MailRequestor mail,
            LogService logService) {
        requireNonNull(config, "config");
        requireNonNull(cliConfig, "cliConfig");
        requireNonNull(queue, "queue");
        requireNonNull(gcQueue, "gcQueue");
        requireNonNull(failedImportLogStore, "failedImportLogStore");
        requireNonNull(mail, "mail");
        requireNonNull(logService, "logService");
        
        this.config = config;
        this.cliConfig = cliConfig;
        this.queue = queue;
        this.gcQueue = gcQueue;
        this.failedImportLogStore = failedImportLogStore;
        this.mail = mail;
        this.log = new ImportLogger(logService);
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
    
    public TaskFileStore<ImportId> failedImportLogStore() {
        return failedImportLogStore;
    }
    
    public MailRequestor mail() {
        return mail;
    }
    
    public ImportLogger log() {
        return log;
    }
    
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return new ImportLogPath(config().importLogDir(), taskId);
    }
    
    public Path failedImportLogPathFor(ImportId taskId) {
        Path importLog = importLogPathFor(taskId).get();
        return config.failedImportLogDir().resolve(importLog.getFileName());
    }
    
    public FutureTimepoint importLogRetentionFromNow() {
        return new FutureTimepoint(config().logRetentionPeriod());
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().importLogDir());
        ensureDirectory(config().failedImportLogDir());
    }
    
}
