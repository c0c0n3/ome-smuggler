package ome.smuggler.config.wiring.imports;

import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.service.file.impl.KeyValueFileStore;
import ome.smuggler.core.service.file.impl.TSafeKeyValueStore;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.*;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.file.impl.TaskIdPathStore;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportLogDisposer;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.service.imports.impl.ImportEnv;
import ome.smuggler.core.service.imports.impl.ImportFailureHandler;
import ome.smuggler.core.service.imports.impl.ImportLogDeleteAction;
import ome.smuggler.core.service.imports.impl.ImportMonitor;
import ome.smuggler.core.service.imports.impl.ImportRunner;
import ome.smuggler.core.service.imports.impl.ImportTrigger;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.providers.log.LogAdapter;

/**
 * Spring bean wiring configuration for the import service interfaces.
 */
@Configuration
public class ImportServiceBeans {

    @Bean
    public TaskFileStore<ImportId> failedImportLogStore(
            ImportConfigSource config) {
        return new TaskIdPathStore<>(config.failedImportLogDir(), 
                                     ImportId::new);
    }

    @Bean
    public KeyValueStore<ImportBatchId, ImportBatchStatus> batchStore(
            ImportConfigSource config) {
        TaskFileStore<ImportBatchId> backingStore = new TaskIdPathStore<>(
                config.batchStatusDbDir(),
                ImportBatchId::new);
        KeyValueStore<ImportBatchId, ImportBatchStatus> store =
                new KeyValueFileStore<>(
                        backingStore,
                        new JsonInputStreamReader<>(ImportBatchStatus.class),
                        new JsonOutputStreamWriter<>());
        return new TSafeKeyValueStore<>(store,
                                        config.batchStatusDbLockStripes());
    }

    @Bean
    public ImportEnv importEnv(
            ImportConfigSource config,
            SessionService session,
            ImportService importer,
            ChannelSource<QueuedImport> importSourceChannel,
            SchedulingSource<ImportLogFile> importGcSourceChannel,
            KeyValueStore<ImportBatchId, ImportBatchStatus> batchStore,
            TaskFileStore<ImportId> failedImportLogStore,
            MailRequestor mail, 
            MailConfigSource mailConfig) {
        ImportEnv env = new ImportEnv(config, session, importer,
                                      importSourceChannel,
                                      importGcSourceChannel,
                                      batchStore,
                                      failedImportLogStore, mail, 
                                      mailConfig.sysAdminAddress(), 
                                      new LogAdapter());
        env.ensureDirectories();
        return env;
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

    @Bean
    public ImportTracker importTracker(ImportEnv env) {
        return new ImportMonitor(env);
    }

}
