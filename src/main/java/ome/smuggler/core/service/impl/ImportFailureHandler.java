package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

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
    
    @Override
    public void accept(QueuedImport task) {
        requireNonNull(task, "task");
        
        ImportId taskId = task.getTaskId();
        Path importLog = env.importLogPathFor(taskId).get();
        if (Files.exists(importLog)) {
            Path target = env.failedImportLogPathFor(taskId);
            unchecked(() -> { Files.copy(importLog, target, 
                                       StandardCopyOption.REPLACE_EXISTING); });
        }
    }

}
