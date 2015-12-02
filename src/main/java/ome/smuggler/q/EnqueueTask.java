package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;

import java.util.function.Function;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.MessageSource;

/**
 * Puts messages on a queue, asynchronously.
 * Messages are durable by default but any other kind of message can be
 * constructed by providing a message builder function as message metadata.
 */
public class EnqueueTask<T> 
    implements MessageSource<Function<QueueConnector, ClientMessage>, T> {

    private final QueueConnector queue;
    private final ClientProducer producer;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages. 
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public EnqueueTask(QueueConnector queue) throws HornetQException {
        requireNonNull(queue, "queue");
        
        this.queue = queue;
        this.producer = queue.newProducer();
    }

    @Override
    public void send(
            ChannelMessage<Function<QueueConnector, ClientMessage>, T> msg) 
                    throws Exception {
        requireNonNull(msg, "msg");
        
        Function<QueueConnector, ClientMessage> messageBuilder = 
                msg.metadata().orElse(QueueConnector::newDurableMessage);
        ClientMessage qMsg = messageBuilder.apply(queue);
        writeBody(qMsg, msg.data());
        producer.send(qMsg);
    }

}
