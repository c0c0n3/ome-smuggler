package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Message to request that the OMERO session of a queued import be kept alive.
 * This message goes on the keep-alive queue repeatedly at configured intervals
 * until the import is run. At that point one last message is put on the queue
 * to signal that the session keep-alive is no longer needed.
 */
public class ImportKeepAlive {
    
    /**
     * Creates a new request to keep an import session alive.
     * @param importRequest details the import.
     * @return the message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ImportKeepAlive keepAliveMessage(QueuedImport importRequest) {
        return new ImportKeepAlive(importRequest, false);
    }
    
    /**
     * Creates a new request to stop keeping an import session alive.
     * @param importRequest details the import.
     * @return the message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ImportKeepAlive stopKeepAliveMessage(
            QueuedImport importRequest) {
        return new ImportKeepAlive(importRequest, true);
    }
    
    
    private final QueuedImport importRequest;
    private final boolean stop;


    public ImportKeepAlive(QueuedImport importRequest, boolean stop) {
        requireNonNull(importRequest, "importRequest");
        
        this.importRequest = importRequest;
        this.stop = stop;
    }
    
    /**
     * @return the details of the import whose session we need to keep alive.
     */
    public QueuedImport importRequest() {
        return importRequest;
    }
    
    /**
     * @return {@code true} if this is the keep-alive is no longer needed;
     * {@code false} otherwise.
     */
    public boolean stop() {
        return stop;
    }
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportKeepAlive) {
            ImportKeepAlive other = (ImportKeepAlive) x;
            return Objects.equals(importRequest, other.importRequest)
                && Objects.equals(stop, other.stop);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(importRequest, stop);
    }
    
}
