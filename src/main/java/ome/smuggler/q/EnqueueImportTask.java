package ome.smuggler.q;

import static java.util.Objects.requireNonNull;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;

/**
 * Triggers an OMERO import.
 */
public class EnqueueImportTask implements ImportRequestor {

    private final ImportQConfig config;
    private final ClientSession session;
    private final ClientProducer producer;
    
    public EnqueueImportTask(ImportQConfig config, ClientSession session) 
            throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        
        this.config = config;
        this.session = session;
        this.producer = session.createProducer(config.getAddress());
    }
    
    @Override
    public ImportId enqueue(ImportInput request) {
        return new ImportId();
    }

}
