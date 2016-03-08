package ome.smuggler.core.service.imports;

import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

/**
 * Triggers an OMERO import.
 */
public interface ImportRequestor {

    /**
     * Adds the import request to the queue and returns immediately. 
     * The request will subsequently be fetched from the import queue and 
     * serviced as needed.
     * What is added to the queue is an instance of {@link QueuedImport}.
     * @param request details what to import.
     * @return a token to use to get hold of this import run, for example to
     * get some feedback on progress.
     * @throws NullPointerException if the argument is {@code null}.
     */
    ImportId enqueue(ImportInput request);
    
}
