package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.service.impl.Loggers.logImportPermanentFailure;
import static util.error.Exceptions.runUnchecked;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import ome.smuggler.core.service.FailedImportHandler;
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
        if (Files.exists(importLog)) {
            Path target = env.failedImportLogPathFor(taskId);
            runUnchecked(() -> Files.copy(importLog, target, 
                                          StandardCopyOption.REPLACE_EXISTING));
        }
    }
    
    @Override
    public void accept(QueuedImport task) {
        requireNonNull(task, "task");
        
        logImportPermanentFailure(task);
        storeAsFailedImport(task);
    }

}
