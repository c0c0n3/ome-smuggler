package ome.smuggler.core.convert;

import static util.error.Exceptions.unchecked;

/**
 * Reads a value from a data source, typically the body of a message fetched 
 * from a queue. 
 * @see SinkWriter
 */
public interface SourceReader<T> {

    /**
     * Reads {@code T}-value from the underlying data source.
     * @return the value read from the source.
     * @throws Exeption if the value could not be read.
     */
    T read() throws Exception;

    /**
     * Calls the {@link #read() read} method converting any checked exception 
     * into an unchecked one that will bubble up without requiring a {@code 
     * throws} clause on this method.
     * @return whatever {@code read} would return.
     */
    default T uncheckedRead() {
        return unchecked(this::read).get();
    }

}
