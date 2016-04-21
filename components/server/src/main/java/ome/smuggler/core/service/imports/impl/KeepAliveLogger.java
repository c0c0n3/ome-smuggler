package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;
import util.object.Pair;

import java.util.stream.Stream;

import static util.object.Pair.pair;

/**
 * Logging methods for the OMERO import session keep-alive task.
 */
public class KeepAliveLogger extends BaseLogger {

    private static Stream<Pair<Object, Object>> summary(QueuedImport data) {
        ImportInput req = data.getRequest();
        return Stream.of(
                pair("Import ID", data.getTaskId().id()),
                pair("Session ID", req.getSessionKey()),
                pair("Import Target", req.getTarget()));
    }

    public KeepAliveLogger(LogService service) {
        super(service);
    }

    public void successful(QueuedImport data) {
        info(data, fieldsWriter("Executed keep-alive for", summary(data)));
    }

    public void failed(QueuedImport data) {
        error(data, fieldsWriter("Failed keep-alive for", summary(data)));
    }

}
