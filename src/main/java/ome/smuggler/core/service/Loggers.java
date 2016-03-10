package ome.smuggler.core.service;

import static util.string.Strings.write;

import java.io.PrintWriter;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility log actions.
 */
public class Loggers {

    /**
     * Gets a logger for the given object.
     * @param x the target object.
     * @return the logger.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Logger loggerFor(Object x) {
        return LoggerFactory.getLogger(x.getClass().getName());
    }
    
    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace.
     * @param site where the error was detected.
     * @param error the error that was caught.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void logTransientError(Object site, Exception error) {
        Logger logger = loggerFor(site);
        logger.warn(error.toString());
        logger.debug(write(buf -> error.printStackTrace(buf)));
    }
    
    public static void logInfo(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).info(write(messageWriter));
    }
    
    public static void logWarn(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).warn(write(messageWriter));
    }
    
    public static void logError(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).error(write(messageWriter));
    }
    
}
