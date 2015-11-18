package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.service.impl.ImportOutput;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

/**
 * Triggers an OMERO import.
 */
public class EnqueueImportTask implements ImportRequestor {

    private final ClientSession session;
    private final ClientProducer producer;
    private final ImportLogConfig logConfig;
    
    public EnqueueImportTask(ImportQConfig qConfig, ImportLogConfig logConfig,
                             ClientSession session) 
            throws HornetQException {
        requireNonNull(qConfig, "qConfig");
        requireNonNull(logConfig, "logConfig");
        requireNonNull(session, "session");
        
        this.session = session;
        this.producer = session.createProducer(qConfig.getAddress());
        this.logConfig = logConfig;
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
    
    private void notifyQueued(QueuedImport task) {
        ImportOutput out = new ImportOutput(logConfig, task);
        try {
            out.writeQueued();
        } catch (IOException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    @Override
    public ImportId enqueue(ImportInput request) {
        ImportId taskId = new ImportId();
        QueuedImport task = new QueuedImport(taskId, request);
        notifyQueued(task);
        sendMessage(task);
        
        return taskId;
    }

}
