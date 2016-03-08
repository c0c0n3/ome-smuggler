package ome.smuggler.core.service;

import static java.util.stream.Collectors.joining;
import static util.object.Pair.pair;
import static util.string.Strings.write;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

/**
 * Utility log actions.
 */
public class Loggers {

    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace.
     * @param site where the error was detected.
     * @param error the error that was caught.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void logTransientError(Object site, Exception error) {
        Logger logger = LoggerFactory.getLogger(site.getClass().getName());
        logger.warn(error.toString());
        logger.debug(write(buf -> error.printStackTrace(buf)));
    }
    
    /**
     * Logs an error when an import fails permanently, after having been 
     * retried.
     * @param failed details of the original import request.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void logImportPermanentFailure(QueuedImport failed) {
        Logger logger = LoggerFactory.getLogger(failed.getClass().getName());
        ImportInput req = failed.getRequest();
        String fields = Stream.of(
                pair("Import ID", failed.getTaskId().id()),
                pair("Experimenter Email", req.getExperimenterEmail().get()),
                pair("Import Target", req.getTarget()),
                pair("Server", req.getOmeroHost()))
            .map(p -> String.format(">>> %s: %s%n", p.fst(), p.snd()))
            .collect(joining());
        logger.error(write(buf -> {
            buf.println("OMERO import task failed permanently:");
            buf.println(fields);
        }));
    }
    
}
