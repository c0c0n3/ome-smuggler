package ome.smuggler.q;

import ome.smuggler.core.data.ImportId;
import ome.smuggler.core.data.ImportInput;
import ome.smuggler.core.service.ImportRequestor;

/**
 * Triggers an OMERO import.
 */
public class EnqueueTask implements ImportRequestor {

    @Override
    public ImportId enqueue(ImportInput request) {
        return new ImportId();
    }

}
