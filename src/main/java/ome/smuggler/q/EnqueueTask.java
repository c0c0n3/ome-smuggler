package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.writeBody;

import java.util.function.Function;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;

import ome.smuggler.core.msg.ChannelSource;

/**
 * Puts messages on a queue, asynchronously.
 * Messages are durable by default but any other kind of messages can be
 * constructed by providing a {@link #EnqueueTask(QueueConnector, Function)
 * message builder} or by sub-classing and overriding the 
 * {@link #newMessage(QueueConnector) newMessage} method. 
 */
public class EnqueueTask<T> implements ChannelSource<T> {

    private final QueueConnector queue;
    private final ClientProducer producer;
    private final Function<QueueConnector, ClientMessage> messageBuilder;
    
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
        this.messageBuilder = this::newMessage;
    }
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages.
     * @param messageBuilder called to create a new message to {@link 
     * #send(Object) send} data; useful if you need to customize the HornetQ
     * message to send. 
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public EnqueueTask(QueueConnector queue, 
            Function<QueueConnector, ClientMessage> messageBuilder) 
                    throws HornetQException {
        requireNonNull(queue, "queue");
        
        this.queue = queue;
        this.producer = queue.newProducer();
        this.messageBuilder = this::newMessage;
    }
    
    protected ClientMessage newMessage(QueueConnector queue) {
        return queue.newDurableMessage();
    }
    
    @Override
    public void send(T data) throws Exception {
        ClientMessage msg = messageBuilder.apply(queue);
        writeBody(msg, data);
        producer.send(msg);
    }

}
