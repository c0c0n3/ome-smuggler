package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;

public class ImportOutcomeNotifier {

    private final ImportEnv env;
    private final QueuedImport task;
    
    public ImportOutcomeNotifier(ImportEnv env, QueuedImport task) {
        requireNonNull(env, "env");
        requireNonNull(task, "task");
        
        this.env = env;
        this.task = task;
    }
    
    public void tellSuccess() {
        PlainTextMail message = ImporMailFormatter.successMessage(task);
        env.mail().enqueue(message);
    }
    
    public void tellFailure() {
        PlainTextMail message = ImporMailFormatter.failureMessage(task);
        env.mail().enqueue(message);
    }
    
}
