package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;

import java.util.function.Function;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;

import ome.smuggler.core.msg.ConfigurableChannelSource;

/**
 * Puts messages on a queue, asynchronously.
 * Messages are durable by default but any other kind of messages can be
 * constructed by providing a {@link #EnqueueTask(QueueConnector, Function)
 * message builder} or by sub-classing and overriding the 
 * {@link #newMessage(QueueConnector) newMessage} method. 
 */
public class EnqueueTask<T> 
    implements ConfigurableChannelSource<Function<QueueConnector, ClientMessage>,T> {

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
    public void send(T data) throws Exception {
        send(QueueConnector::newDurableMessage, data);
    }

    @Override
    public void send(Function<QueueConnector, ClientMessage> messageBuilder, 
                     T data) throws Exception {
        requireNonNull(messageBuilder, "messageBuilder");
        requireNonNull(data, "data");
        
        ClientMessage msg = messageBuilder.apply(queue);
        writeBody(msg, data);
        producer.send(msg);
    }

}
