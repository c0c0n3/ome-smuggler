package ome.smuggler.core.service;

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
     * @param taskId identifies the import run.
     * @return the path to the failed import log file.
     */
    Path failedImportLogPathFor(ImportId taskId);
    
    /**
     * @return a list of the imports that failed permanently, i.e. after having
     * being retried.
     */
    Stream<ImportId> listFailedImports();
    
}
