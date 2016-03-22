package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.throwAsIfUnchecked;
import static ome.smuggler.core.types.ImportKeepAlive.keepAliveMessage;

import java.io.IOException;

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

public class ImportTrigger implements ImportRequestor {

    private final ImportEnv env;
    
    public ImportTrigger(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    private void notifyQueued(QueuedImport task) {
        ImportOutput out = new ImportOutput(
                env.importLogPathFor(task.getTaskId()), task);
        try {
            out.writeQueued();
        } catch (IOException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    private void startSessionKeepAlive(QueuedImport task) {
        env.keepAliveQueue().uncheckedSend(keepAliveMessage(task));
    }
    
    @Override
    public ImportId enqueue(ImportInput request) {
        ImportId taskId = new ImportId();
        QueuedImport task = new QueuedImport(taskId, request);
        
        notifyQueued(task);
        env.queue().uncheckedSend(task);
        startSessionKeepAlive(task);
        
        env.log().importQueued(task);
        return taskId;
    }

}
