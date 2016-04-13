package ome.smuggler.core.service.imports;

import ome.smuggler.core.types.ImportLogFile;

/**
 * Disposes of import log files that are no longer needed.
 */
public interface ImportLogDisposer {

    /**
     * Consumes a queued request to remove the specified log file.
     * @param expiredFile file to remove.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void dispose(ImportLogFile expiredFile);
    
}
