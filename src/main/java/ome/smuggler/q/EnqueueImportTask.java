package ome.smuggler.q;

import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;

/**
 * Triggers an OMERO import.
 */
public class EnqueueImportTask implements ImportRequestor {

    @Override
    public ImportId enqueue(ImportInput request) {
        return new ImportId();
    }

}
