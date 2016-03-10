package ome.smuggler.core.types;

import java.nio.file.Path;

import util.object.Identifiable;

/**
 * Path to the file storing an email message that we failed to send.
 */
public class FailedMailPath extends TaskIdPath {

    public FailedMailPath(Path baseDir, Identifiable taskId) {
        super(baseDir, taskId);
    }

}
