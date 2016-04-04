package ome.smuggler.core.service.imports.impl;

import static util.object.Pair.pair;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;
import util.object.Pair;

/**
 * Logging methods for the import service.
 */
public class ImportLogger extends BaseLogger {

    private static Stream<Pair<Object, Object>> summary(QueuedImport data) {
        ImportInput req = data.getRequest();
        return Stream.of(
                pair("Import ID", data.getTaskId().id()),
                pair("Experimenter Email", req.getExperimenterEmail().get()),
                pair("Import Target", req.getTarget()),
                pair("Server", req.getOmeroHost()));
    }
    
    private static Consumer<PrintWriter> writer(String header, 
            QueuedImport data) {
        return fieldsWriter(header, summary(data));
    }

    private final KeepAliveLogger keepAlive;

    public ImportLogger(LogService service) {
        super(service);
        keepAlive = new KeepAliveLogger(service);
    }

    public void importQueued(QueuedImport data) {
        info(data, writer("OMERO import task queued.", data));
    }
    
    public void importStart(QueuedImport data) {
        info(data, writer("OMERO import task starting.", data));
    }
    
    public void importSuccessful(QueuedImport data) {
        info(data, writer("OMERO import task completed successfully.", data));
    }
    
    /**
     * Logs an error when an import fails permanently, after having been 
     * retried.
     * @param data details of the original import request.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void importPermanentFailure(QueuedImport data) {
        error(data, writer("OMERO import task failed permanently.", data));
    }

    public KeepAliveLogger keepAlive() {
        return keepAlive;
    }

}
