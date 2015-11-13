package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

/**
 * Represents an import that has been queued and is waiting to be serviced.
 */
public class QueuedImport {

    private final ImportId taskId;
    private final ImportInput request;
    
    /**
     * Creates a new instance.
     * @param taskId the ID assigned to the import task.
     * @param request the data detailing what the import task must do.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public QueuedImport(ImportId taskId, ImportInput request) {
        requireNonNull(taskId, "taskId");
        requireNonNull(request, "request");
        
        this.taskId = taskId;
        this.request = request;
    }

    /**
     * @return the ID assigned to the import task.
     */
    public ImportId getTaskId() {
        return taskId;
    }

    /**
     * @return the data detailing what the import task must do.
     */
    public ImportInput getRequest() {
        return request;
    }
    
}
