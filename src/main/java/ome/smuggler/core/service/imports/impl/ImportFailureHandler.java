package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.service.Loggers.logImportPermanentFailure;

import java.nio.file.Path;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.QueuedImport;


public class ImportFailureHandler implements FailedImportHandler {

    private final ImportEnv env;
    
    public ImportFailureHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    private void storeAsFailedImport(QueuedImport task) {
        ImportId taskId = task.getTaskId();
        Path importLog = env.importLogPathFor(taskId).get();
        Path target = env.failedImportLogPathFor(taskId);
        FileOps.copy(importLog, target);
    }
    
    @Override
    public void accept(QueuedImport task) {
        requireNonNull(task, "task");
        
        logImportPermanentFailure(task);
        storeAsFailedImport(task);
    }

}
