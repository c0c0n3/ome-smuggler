package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

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
        
        env.failedImportLogStore().add(taskId, importLog);
    }
    
    @Override
    public void accept(QueuedImport task) {
        requireNonNull(task, "task");
        
        env.log().importPermanentFailure(task);
        new ImportOutcomeNotifier(env, task).tellFailure();
        storeAsFailedImport(task);
    }

}
