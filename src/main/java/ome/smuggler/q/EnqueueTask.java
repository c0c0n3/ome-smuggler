package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.core.config.CoreQueueConfiguration;

import ome.smuggler.core.msg.ChannelSource;

/**
 * Puts messages on a queue, asynchronously. 
 */
public class EnqueueTask<T> implements ChannelSource<T> {

    private final ClientSession session;
    private final ClientProducer producer;
    
    /**
     * Creates a new instance.
     * @param config the queue to send messages to.
     * @param session the session to use.
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public EnqueueTask(CoreQueueConfiguration config, ClientSession session) 
            throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        
        this.session = session;
        this.producer = session.createProducer(config.getAddress());
    }
    
    @Override
    public void send(T data) throws Exception {
        ClientMessage msg = session.createMessage(true);  //NB durable msg
        writeBody(msg, data);
        producer.send(msg);
    }

}
