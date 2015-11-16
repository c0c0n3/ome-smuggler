package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.readBody;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.MessageHandler;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.types.QueuedImport;

/**
 * Fetches an OMERO import request from the queue and dispatches it to an
 * {@link ImportProcessor}.
 */
public class DequeueImportTask implements MessageHandler {

    private final ClientConsumer consumer;
    private final ImportProcessor processor;
    
    public DequeueImportTask(ImportQConfig config, ClientSession session,
            ImportProcessor processor) throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        requireNonNull(processor, "processor");
        
        this.processor = processor;
        this.consumer = session.createConsumer(config.getName(), false);
        this.consumer.setMessageHandler(this);
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        QueuedImport request = readBody(msg, QueuedImport.class);
        processor.consume(request);
    }
    
}
