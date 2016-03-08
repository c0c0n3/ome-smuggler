package ome.smuggler.core.service.imports;

import java.nio.file.Path;
import java.util.stream.Stream;

import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportLogPath;

/**
 * Tracking of OMERO imports.
 */
public interface ImportTracker {

    /**
     * Determines the path to the import log for a given import.
     * @param taskId identifies the import run.
     * @return the path to the import log file.
     */
    ImportLogPath importLogPathFor(ImportId taskId);
    
    /**
     * Determines the path to the import log for a given failed import.
     * @param taskId identifies the failed import run.
     * @return the path to the failed import log file.
     */
    Path failedImportLogPathFor(ImportId taskId);
    
    /**
     * @return a list of the imports that failed permanently, i.e. after having
     * being retried.
     */
    Stream<ImportId> listFailedImports();
    
    /**
     * Stops tracking a failed import so that its {@link ImportId} will not be
     * returned anymore among the {@link #listFailedImports() list of failed}
     * imports. This method should be called after the system administrator has
     * resolved the cause of the failure and there is no need to keep the log
     * file around anymore.
     * @param taskId identifies the failed import run.
     */
    void stopTrackingFailedImport(ImportId taskId);
    
}
