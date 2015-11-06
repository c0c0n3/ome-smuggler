package util.string;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.isNullOrZeroLength;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * String utilities
 */
public class Strings {

    /**
     * Is this a {@code null} or empty string?
     * @param x the string to test.
     * @return {@code true} for yes, {@code false} for no. 
     */
    public static boolean isNullOrEmpty(String x) {
        return x == null || x.isEmpty();
    }
    
    /**
     * Gives a {@link Consumer} a {@link PrintWriter} to write some text to.
     * @param writer the consumer that will produce the text.
     * @return the text produced by the consumer.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String write(Consumer<PrintWriter> writer) {
        requireNonNull(writer, "writer");
        
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        
        writer.accept(out);
        out.flush();
        
        return buffer.toString();
    }
    
    /**
     * Throws an exception if the given string is {@link #isNullOrEmpty(String) 
     * null or empty}.
     * @param x the string to test.
     * @param message optional exception message; ignored if {@code null} or
     * empty.
     * @throws IllegalArgumentException if {@code x} is {@code null} or empty.
     */
    public static void requireString(String x, String message) {
        if (isNullOrEmpty(x)) {
            if(isNullOrEmpty(message)) throw new IllegalArgumentException();
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Throws an exception if the given string is {@link #isNullOrEmpty(String) 
     * null or empty}.
     * @param x the string to test.
     * @throws IllegalArgumentException if {@code x} is {@code null} or empty.
     */
    public static void requireString(String x) {
        requireString(x, null);
    }
    
    /**
     * Throws an exception if the argument is {@code null} or has zero length
     * or any of the elements is {@code null} or empty.
     * @param xs the array to test.
     * @throws IllegalArgumentException if the argument is {@code null} or has
     * zero length or any of the components is {@code null} or empty.
     */
    public static void requireStrings(String[] xs) {
        if (isNullOrZeroLength(xs)) {
            throw new IllegalArgumentException("no strings");
        }
        for (int k = 0; k < xs.length; ++k) {
            requireString(xs[k], "null or empty at " + k);
        }
    }
    
    /**
     * Reads the whole input into a UTF-8 string.
     * @param source the data to read; the stream will be closed when this 
     * method returns, even if an exception is thrown.
     * @return the whole content of the stream as a string.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an error occurs while reading the stream.
     */
    public static String readAsString(InputStream source) throws IOException {
        requireNonNull(source, "source");
        
        InputStreamReader in = new InputStreamReader(source, 
                                                     StandardCharsets.UTF_8);
        return readAsString(in);
    }
    
    /**
     * Reads the whole input into a UTF-8 string.
     * @param source the data to read; the source will be closed when this 
     * method returns, even if an exception is thrown.
     * @return the whole input data provided by the source as a string.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an error occurs while reading from the source.
     */
    public static String readAsString(Readable source) throws IOException {
        requireNonNull(source, "source");
        
        Scanner reader = new Scanner(source);
        try {
            reader.useDelimiter("\\A");
            String input = reader.hasNext() ? reader.next() : "";
            IOException maybeError = reader.ioException();
            if (maybeError != null) {
                throw maybeError;
            }
            return input;
        }
        finally {
            reader.close();
        }
    }
    /* Adapted from:
     * - https://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
     */
    
    /**
     * Breaks up the given text into lines.
     * @param text the input text.
     * @return the sequence of lines making up text.
     * @throws NullPointerException if {@code null} arguments.
     */
    public static Stream<String> lines(String text) {
        requireNonNull(text);
        
        Scanner reader = new Scanner(text);
        List<String> lines = new ArrayList<>();
        while (reader.hasNextLine()) {
            lines.add(reader.nextLine());
        }
        reader.close();
        
        return lines.stream();
    }
    
}
