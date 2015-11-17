package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import util.object.Wrapper;

/**
 * A path to the log file of an import run.
 */
public class ImportLogPath extends Wrapper<Path> {
    
    private static Path logPath(Path importLogDir, ImportId taskId) {
        requireNonNull(importLogDir, "importLogDir");
        requireNonNull(taskId, "taskId");
        
        return Paths.get(importLogDir.toString(), taskId.id());
    }
    
    /**
     * Creates a new import log path where to store the output of the specified
     * import run.
     * @param importLogDir the directory in which the import logs are kept.
     * @param taskId the id of the import run.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImportLogPath(Path importLogDir, ImportId taskId) {
        super(logPath(importLogDir, taskId));
    }

}
