package ome.smuggler.core.service.file;

import java.nio.file.Path;
import java.util.stream.Stream;

import util.object.Identifiable;

/**
 * Manages a directory containing files associated to {@link Identifiable} task 
 * ID's.
 */
public interface TaskFileStore<T extends Identifiable> {

    /**
     * Builds the path to the file associated to the given task ID.
     * @param taskId the task ID.
     * @return the path associated to the ID.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Path pathFor(T taskId);

    /**
     * @return the task ID's of all files currently stored in the directory.
     */
    public Stream<T> listTaskIds();
    
    /**
     * Deletes the file associated to the specified task ID.
     * @param taskId the task ID.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void remove(T taskId);

}
