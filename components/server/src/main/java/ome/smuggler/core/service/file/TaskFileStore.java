package ome.smuggler.core.service.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import util.lambda.ConsumerE;
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
    Path pathFor(T taskId);

    /**
     * @return the task ID's of all files currently stored in the directory.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * runtime exception and re-thrown as is.
     */
    Stream<T> listTaskIds();
    
    /**
     * Deletes the file associated to the specified task ID.
     * @param taskId the task ID.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * runtime exception and re-thrown as is.
     */
    void remove(T taskId);

    /**
     * Writes a file associated to the specified task.
     * If the file already exists, it will be overwritten.
     * @param taskId identifies the task.
     * @param contentWriter writes the file contents into an output stream.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * runtime exception and re-thrown as is.
     */
    void add(T taskId, ConsumerE<OutputStream> contentWriter);
    
    /**
     * Writes a file associated to the specified task.
     * If the file already exists, it will be overwritten.
     * @param taskId identifies the task.
     * @param content the file contents.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * runtime exception and re-thrown as is.
     */
    default void add(T taskId, String content) {
        add(taskId, out -> {
            PrintStream writer = new PrintStream(out);
            writer.print(content);
        });
    }
    
    /**
     * Writes a file associated to the specified task by taking the contents
     * from a source file.
     * If the file already exists in the store, it will be overwritten.
     * (The source file is never modified though.)
     * @param taskId identifies the task.
     * @param contentSource the file contents to use.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * runtime exception and re-thrown as is.
     */
    void add(T taskId, Path contentSource);
    
}
