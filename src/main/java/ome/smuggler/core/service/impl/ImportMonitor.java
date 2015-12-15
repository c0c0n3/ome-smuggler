package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.stream.Stream;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.service.ImportTracker;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportLogPath;

/**
 * Implementation of the {@link ImportTracker import tracking} service.
 */
public class ImportMonitor implements ImportTracker {

    private final ImportEnv env;
    
    /**
     * Creates a new instance.
     * @param env the import environment to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportMonitor(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    @Override
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return env.importLogPathFor(taskId);
    }

    @Override
    public Path failedImportLogPathFor(ImportId taskId) {
        return env.failedImportLogPathFor(taskId);
    }

    @Override
    public Stream<ImportId> listFailedImports() {
        Path failedImportLogDir = env.config().failedImportLogDir();
        return FileOps.listChildFiles(failedImportLogDir)
                      .map(Path::getFileName)
                      .map(Path::toString)
                      .map(ImportId::new);
    }

    @Override
    public void stopTrackingFailedImport(ImportId taskId) {
        FileOps.delete(failedImportLogPathFor(taskId));
    }

}
