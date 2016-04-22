package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import java.util.Optional;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.*;

/**
 * Provides access to import configuration and queues.
 */
public class ImportEnv {
    
    private final ImportConfigSource config;
    private final SessionService session;
    private final ImportService importer;
    private final ChannelSource<QueuedImport> queue;
    private final SchedulingSource<ImportLogFile> gcQueue;
    private final TaskFileStore<ImportId> failedImportLogStore;
    private final MailRequestor mail;
    private final Optional<Email> sysAdminEmail; 
    private final ImportLogger log;
    
    public ImportEnv(ImportConfigSource config, SessionService session,
                     ImportService importer,
            ChannelSource<QueuedImport> queue, 
            SchedulingSource<ImportLogFile> gcQueue,
            TaskFileStore<ImportId> failedImportLogStore,
            MailRequestor mail,
            Optional<Email> sysAdminEmail,
            LogService logService) {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        requireNonNull(importer, "importer");
        requireNonNull(queue, "queue");
        requireNonNull(gcQueue, "gcQueue");
        requireNonNull(failedImportLogStore, "failedImportLogStore");
        requireNonNull(mail, "mail");
        requireNonNull(sysAdminEmail, "sysAdminEmail");
        requireNonNull(logService, "logService");
        
        this.config = config;
        this.session = session;
        this.importer = importer;
        this.queue = queue;
        this.gcQueue = gcQueue;
        this.failedImportLogStore = failedImportLogStore;
        this.mail = mail;
        this.sysAdminEmail = sysAdminEmail;
        this.log = new ImportLogger(logService);
    }
    
    public ImportConfigSource config() {
        return config;
    }
    
    public SessionService session() {
        return session;
    }

    public ImportService importer() {
        return importer;
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
