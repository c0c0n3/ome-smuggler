package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Supplier;
import java.util.stream.Stream;

import util.lambda.FunctionE;

/**
 * Utility methods for I/O steam operations.
 */
public class StreamOps {

    /**
     * Builds a stream of lines out of the input data and passes the stream on 
     * to the given reader to convert them into a value of type {@code T}.
     * @param input the input data.
     * @param reader converts the stream of lines into a {@code T-}value.
     * @param encoding character encoding to use to read the input data; if not
     * given, then the default character encoding is used. 
     * @return the value returned by the reader.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * a runtime exception and thrown as is without wrapping.
     */
    public static <T> T readLines(InputStream input, 
                                  FunctionE<Stream<String>, T> reader,
                                  Charset...encoding) {
        requireNonNull(input, "input");
        requireNonNull(reader, "reader");
        
        Supplier<InputStreamReader> streamReader = 
             (encoding == null || encoding.length == 0 || encoding[0] == null) ?
                     () -> new InputStreamReader(input) :
                     () -> new InputStreamReader(input, encoding[0]);
        
        try (BufferedReader buffer = new BufferedReader(streamReader.get())) {
            return reader.apply(buffer.lines());
        } catch (IOException e) {
            throwAsIfUnchecked(e);
            return null;  // never reached, keeps compiler happy tho. 
        }
    }

    /**
     * Reads the whole input stream into a string using the default character
     * encoding.
     * @param input the data to read.
     * @return the input as string.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an I/O error occurs; the exception is masked as
     * a runtime exception and thrown as is without wrapping.
     */
    public static String readLinesIntoString(InputStream input) {
        String sep = System.lineSeparator();
        return readLines(input, lines -> lines.collect(joining(sep)));
    }

    /**
     * Closes the given stream, silently swallowing any {@link IOException}.
     * @param stream the stream to close.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void close(Closeable stream) {
        requireNonNull(stream, "stream");
        
        try {
            stream.close();
        } catch (IOException e) {
            // ignore
        }
    }

}
