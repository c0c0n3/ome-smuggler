package ome.smuggler.core.service.log;

import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Defines the functionality that our services need to log messages.
 */
public interface LogService {

    /**
     * Logs an informational message. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void info(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs a warning. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void warn(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs an error. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void error(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace.
     * @param site where the error was detected.
     * @param error the error that was caught.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void transientError(Object site, Exception error);
    
}
