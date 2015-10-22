package util;

import static java.util.Objects.requireNonNull;
import static util.Arrayz.isNullOrZeroLength;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

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
    
}
