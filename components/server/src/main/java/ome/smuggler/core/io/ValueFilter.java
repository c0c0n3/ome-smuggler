package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * A {@link StreamFilter} that reads and writes {@code T}-values.
 * This filter deserialises the input stream into memory as a {@code T}-value
 * {@code t}, gives {@code t} to a setter function to produce a new {@code
 * T}-value {@code t'}, and then serialises {@code t'} to the output stream.
 */
public class ValueFilter<T> implements StreamFilter {

    private final Function<InputStream, T> reader;
    private final BiConsumer<OutputStream, T> writer;
    private final Function<T, T> setter;

    /**
     * Creates a new instance.
     * @param reader deserialises the input stream.
     * @param writer serialises a {@code T}-value to the output stream.
     * @param setter given the deserialised value, produces the value to
     *               serialise to the output stream.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ValueFilter(Function<InputStream, T> reader,
                       BiConsumer<OutputStream, T> writer,
                       Function<T, T> setter) {
        requireNonNull(reader, "reader");
        requireNonNull(writer, "writer");
        requireNonNull(setter, "setter");

        this.reader = reader;
        this.writer = writer;
        this.setter = setter;
    }

    @Override
    public void processE(InputStream in, OutputStream out) throws Exception {
        requireNonNull(in, "in");
        requireNonNull(out, "out");

        T oldValue = reader.apply(in);
        T newValue = setter.apply(oldValue);
        writer.accept(out, newValue);
    }

}
