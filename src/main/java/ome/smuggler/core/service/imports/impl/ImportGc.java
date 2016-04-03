package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.core.types.ImportKeepAlive.stopKeepAliveMessage;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;

/**
 * Utility to clean up after an import.
 */
public class ImportGc {    
    
    private final ImportEnv env;
    
    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportGc(ImportEnv env) {
        requireNonNull(env, "env");
        
        this.env = env;
    }

    private void scheduleDeletion(QueuedImport task) {
        ImportLogFile logFile = env.importLogPathFor(task.getTaskId()).file();
        FutureTimepoint when = env.importLogRetentionFromNow();
        env.gcQueue().uncheckedSend(message(when, logFile));
    }
    
    private void stopSessionKeepAlive(QueuedImport task) {
        env.keepAliveQueue().uncheckedSend(stopKeepAliveMessage(task));    
    }
    
    /**
     * Performs garbage collection for the given import task. 
     * @param task an import task that has been fully carried out.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void run(QueuedImport task) {
        requireNonNull(task, "task");
        
        scheduleDeletion(task);
        stopSessionKeepAlive(task);
    }
    
}
