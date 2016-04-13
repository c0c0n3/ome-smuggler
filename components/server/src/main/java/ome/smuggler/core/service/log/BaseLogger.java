package ome.smuggler.core.service.log;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import util.object.Pair;

/**
 * Base class for loggers that extend the basic functionality of the 
 * {@link LogService} to provide logging tailored to a particular service.
 */
public class BaseLogger implements LogService {

    /**
     * Prints the given name-value pairs, one per line, into a string.
     * Each line has the format: "{@code >>> name: value}".
     * @param fields the pairs to print.
     * @return a string containing the printed pairs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String fieldsToString(Stream<Pair<Object, Object>> fields) {
        requireNonNull(fields, "fields");
        
        return fields
            .map(p -> String.format(">>> %s: %s%n", p.fst(), p.snd()))
            .collect(joining());
    }
    
    /**
     * Builds a consumer that writes the given name-value pairs to a 
     * {@link PrintWriter} buffer using the {@link #fieldsToString(Stream) 
     * fieldsToString} method.
     * @param header the first line to output.
     * @param fields the name-value pairs to output.
     * @return the consumer.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T> Consumer<PrintWriter> fieldsWriter(String header,
            Stream<Pair<Object, Object>> fields) {
        requireNonNull(header, "header");
        
        return buf -> {
            buf.println(header);
            buf.println(fieldsToString(fields));
        };
    }
    
    private final LogService service;
    
    public BaseLogger(LogService service) {
        this.service = service;
    }

    @Override
    public void debug(Object site, Consumer<PrintWriter> messageWriter) {
        service.debug(site, messageWriter);
    }
    
    @Override
    public void info(Object site, Consumer<PrintWriter> messageWriter) {
        service.info(site, messageWriter);
    }

    @Override
    public void warn(Object site, Consumer<PrintWriter> messageWriter) {
        service.warn(site, messageWriter);
    }

    @Override
    public void error(Object site, Consumer<PrintWriter> messageWriter) {
        service.error(site, messageWriter);
    }

    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace.
     * @param site where the error was detected.
     * @param error the error that was caught.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void transientError(Object site, Exception error) {
        requireNonNull(error, "error");
        
        warn(site, buf -> buf.print(error.toString()));
        debug(site, buf -> error.printStackTrace(buf));
    }
    
}
