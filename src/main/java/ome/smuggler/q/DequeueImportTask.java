package ome.smuggler.q;

import static java.util.Objects.requireNonNull;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.MessageHandler;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportProcessor;

/**
 * Fetches an OMERO import request from the queue and dispatches it to an
 * {@link ImportProcessor}.
 */
public class DequeueImportTask implements MessageHandler {

    private final ImportQConfig config;
    private final ClientSession session;
    private final ClientConsumer consumer;
    private final ImportProcessor processor;
    
    public DequeueImportTask(ImportQConfig config, ClientSession session,
            ImportProcessor processor) throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        requireNonNull(processor, "processor");
        
        this.config = config;
        this.session = session;
        this.processor = processor;
        this.consumer = session.createConsumer(config.getName());
        this.consumer.setMessageHandler(this);
    }

    @Override
    public void onMessage(ClientMessage message) {
        // TODO Auto-generated method stub
    }
    
}
