package ome.smuggler.core.service.imports;

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
     
}
