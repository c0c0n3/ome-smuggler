package ome.smuggler.core.convert;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

/**
 * Writes a value to a data sink, typically the body of a message to put on a 
 * queue. 
 * @see SourceReader
 */
public interface SinkWriter<T> {

    /**
     * Writes a value to the underlying data sink.
     * @param value the data to write.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the data could not be written to the sink.
     */
    void write(T value) throws Exception;
    
    /**
     * Calls the {@link #write(Object) write} method converting any checked 
     * exception into an unchecked one that will bubble up without requiring
     * a {@code throws} clause on this method.
     */
    default void uncheckedWrite(T value) {
        requireNonNull(value, "value");
        unchecked(this::write).accept(value);
    }
    
}
