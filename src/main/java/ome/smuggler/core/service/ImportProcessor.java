package ome.smuggler.core.service;

import ome.smuggler.core.types.QueuedImport;

/**
 * Carries out an OMERO import.
 */
public interface ImportProcessor {

    /**
     * Consumes an import request that was fetched from the import queue.
     * @param request details what to import.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void consume(QueuedImport request);
    
}
