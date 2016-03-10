package ome.smuggler.core.service.imports.impl;

import static java.util.stream.Collectors.joining;
import static util.object.Pair.pair;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.service.Loggers;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

/**
 * Utility logging methods for the import service.
 */
public class ImportLoggers extends Loggers {

    private static String importDataSummary(QueuedImport data) {
        ImportInput req = data.getRequest();
        return Stream.of(
                pair("Import ID", data.getTaskId().id()),
                pair("Experimenter Email", req.getExperimenterEmail().get()),
                pair("Import Target", req.getTarget()),
                pair("Server", req.getOmeroHost()))
            .map(p -> String.format(">>> %s: %s%n", p.fst(), p.snd()))
            .collect(joining());
    }
    
    private static Consumer<PrintWriter> messageWriter(
            QueuedImport data, String event) {
        return buf -> {
            buf.println(event);
            buf.println(importDataSummary(data));
        };
    }
    
    public static void logImportQueued(QueuedImport data) {
        logInfo(data, messageWriter(data, "OMERO import task queued."));
    }
    
    public static void logImportStart(QueuedImport data) {
        logInfo(data, messageWriter(data, "OMERO import task starting."));
    }
    
    public static void logImportSuccessful(QueuedImport data) {
        logInfo(data, 
                messageWriter(data, "OMERO import task completed successfully."));
    }
    
    /**
     * Logs an error when an import fails permanently, after having been 
     * retried.
     * @param failed details of the original import request.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void logImportPermanentFailure(QueuedImport failed) {
        logError(failed, 
                messageWriter(failed, "OMERO import task failed permanently."));
    }

}
