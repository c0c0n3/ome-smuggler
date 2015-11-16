package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;
import static util.error.Exceptions.throwAsIfUnchecked;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

/**
 * Triggers an OMERO import.
 */
public class EnqueueImportTask implements ImportRequestor {

    private final ClientSession session;
    private final ClientProducer producer;
    
    public EnqueueImportTask(ImportQConfig config, ClientSession session) 
            throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        
        this.session = session;
        this.producer = session.createProducer(config.getAddress());
    }
    
    private void sendMessage(QueuedImport task) {
        ClientMessage importMsg = session.createMessage(true);  //NB durable msg
        writeBody(importMsg, task);
        try {
            producer.send(importMsg);
        } catch (HornetQException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    @Override
    public ImportId enqueue(ImportInput request) {
        ImportId taskId = new ImportId();
        QueuedImport task = new QueuedImport(taskId, request);
        sendMessage(task);

        return taskId;
    }

}
