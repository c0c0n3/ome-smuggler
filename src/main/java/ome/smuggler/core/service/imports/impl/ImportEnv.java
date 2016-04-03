package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import java.util.Optional;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportKeepAlive;
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
    private final ChannelSource<ImportKeepAlive> keepAliveQueue;
    private final TaskFileStore<ImportId> failedImportLogStore;
    private final MailRequestor mail;
    private final Optional<Email> sysAdminEmail; 
    private final ImportLogger log;
    
    public ImportEnv(ImportConfigSource config, CliImporterConfig cliConfig,
            ChannelSource<QueuedImport> queue, 
            SchedulingSource<ImportLogFile> gcQueue,
            ChannelSource<ImportKeepAlive> keepAliveQueue,
            TaskFileStore<ImportId> failedImportLogStore,
            MailRequestor mail,
            Optional<Email> sysAdminEmail,
            LogService logService) {
        requireNonNull(config, "config");
        requireNonNull(cliConfig, "cliConfig");
        requireNonNull(queue, "queue");
        requireNonNull(gcQueue, "gcQueue");
        requireNonNull(keepAliveQueue, "keepAliveQueue");
        requireNonNull(failedImportLogStore, "failedImportLogStore");
        requireNonNull(mail, "mail");
        requireNonNull(sysAdminEmail, "sysAdminEmail");
        requireNonNull(logService, "logService");
        
        this.config = config;
        this.cliConfig = cliConfig;
        this.queue = queue;
        this.gcQueue = gcQueue;
        this.keepAliveQueue = keepAliveQueue;
        this.failedImportLogStore = failedImportLogStore;
        this.mail = mail;
        this.sysAdminEmail = sysAdminEmail;
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
    
    public ImportGc garbageCollector() {
        return new ImportGc(this);
    }
    
    public ChannelSource<ImportKeepAlive> keepAliveQueue() {
        return keepAliveQueue;
    }
    
    public TaskFileStore<ImportId> failedImportLogStore() {
        return failedImportLogStore;
    }
    
    public MailRequestor mail() {
        return mail;
    }
    
    public Optional<Email> sysAdminEmail() {
        return sysAdminEmail;
    }
    
    public ImportLogger log() {
        return log;
    }
    
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return new ImportLogPath(config().importLogDir(), taskId);
    }
    
    public FutureTimepoint importLogRetentionFromNow() {
        return new FutureTimepoint(config().logRetentionPeriod());
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().importLogDir());
        ensureDirectory(config().failedImportLogDir());
    }
    
}
