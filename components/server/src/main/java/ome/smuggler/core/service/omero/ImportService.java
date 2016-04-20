package ome.smuggler.core.service.omero;

import ome.smuggler.core.types.ImportInput;

/**
 * Provides access to OMERO import functionality.
 */
public interface ImportService {

    /**
     * Runs an import.
     * @param data details what to import and where.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     */
    boolean importData(ImportInput data);

}
