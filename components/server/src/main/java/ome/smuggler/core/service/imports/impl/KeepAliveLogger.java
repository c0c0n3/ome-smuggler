package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;
import util.object.Pair;
import util.sequence.Streams;

import java.io.PrintWriter;
import java.util.function.Consumer;
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

    private static Stream<Pair<Object, Object>> summary(int exitCode,
                                                        String cmdOutput) {
        return Stream.of(
                pair("Exit Code", exitCode),
                pair("Command Output", cmdOutput));
    }

    private static Stream<Pair<Object, Object>> summary(
            KeepAliveCommandBuilder cmd) {
        return Stream.of(pair("Command Line:", cmd));
    }

    @SafeVarargs
    private static Consumer<PrintWriter> writer(String header,
                                                Stream<Pair<Object, Object>>...xs) {
        return fieldsWriter(header, Streams.concat(xs));
    }

    public KeepAliveLogger(LogService service) {
        super(service);
    }

    public void successful(QueuedImport data, int exitCode,
        String cmdOutput, KeepAliveCommandBuilder cmd) {
        debug(data, writer("Executed keep-alive for",
                            summary(data), summary(exitCode, cmdOutput),
                            summary(cmd)));
    }

    public void failed(QueuedImport data, int exitCode,
                       String cmdOutput, KeepAliveCommandBuilder cmd) {
        String header = "Failed keep-alive for";
        debug(data, writer(header, summary(data), summary(exitCode, cmdOutput),
                            summary(cmd)));
        error(data, writer(header, summary(data), summary(exitCode, cmdOutput)));
    }

    public void failed(QueuedImport data, Exception error) {
        error(data, writer("Failed to launch keep-alive for", summary(data)));
        error(data, error::printStackTrace);
    }

}
