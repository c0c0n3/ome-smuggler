package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents an import outcome notification that has been queued and is waiting
 * to be sent to the intended recipient.
 */
public class ImportNotification {

    private final ImportId taskId;
    private final TextNotification outcome;
    
    /**
     * Creates a new instance.
     * @param taskId the ID assigned to the import task.
     * @param outcome the notification message to send.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImportNotification(ImportId taskId, TextNotification outcome) {
        requireNonNull(taskId, "taskId");
        requireNonNull(outcome, "outcome");
        
        this.taskId = taskId;
        this.outcome = outcome;
    }

    /**
     * @return the ID assigned to the import task.
     */
    public ImportId getTaskId() {
        return taskId;
    }

    /**
     * @return the notification message to send.
     */
    public TextNotification getOutcome() {
        return outcome;
    }
    
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportNotification) {
            ImportNotification other = (ImportNotification) x;
            return Objects.equals(taskId, other.taskId)
                && Objects.equals(outcome, other.outcome);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(taskId, outcome);
    }

}
