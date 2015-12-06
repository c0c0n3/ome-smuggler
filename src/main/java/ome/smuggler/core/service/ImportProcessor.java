package ome.smuggler.core.service;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.types.QueuedImport;

/**
 * Carries out an OMERO import.
 */
public interface ImportProcessor {

    /**
     * Consumes an import request that was fetched from the import queue.
     * @param request details what to import.
     * @return {@link RepeatAction#Repeat Repeat} if the import failed because
     * of a transient error condition and it should be retried; {@link 
     * RepeatAction#Stop Stop} if the import should not be retried because it 
     * succeeded or it failed but it's not possible to recover. 
     * @throws NullPointerException if the argument is {@code null}.
     */
    RepeatAction consume(QueuedImport request);
    
}
