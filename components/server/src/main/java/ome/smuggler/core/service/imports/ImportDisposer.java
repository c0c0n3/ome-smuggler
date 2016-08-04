package ome.smuggler.core.service.imports;

import ome.smuggler.core.types.ProcessedImport;

/**
 * Disposes of import log files that are no longer needed.
 */
public interface ImportDisposer {

    /**
     * Consumes a queued request to remove any resources still associated with
     * it.
     * @param task the import task to clean after.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void dispose(ProcessedImport task);

}
