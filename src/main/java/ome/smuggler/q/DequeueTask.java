package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.readBody;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.util.Optional;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.MessageHandler;

import ome.smuggler.core.msg.ChannelAwareSink;
import ome.smuggler.core.msg.ChannelSink;

/**
 * Receives messages asynchronously from a queue and dispatches them to a 
 * consumer.
 */
public class DequeueTask<T> implements MessageHandler {
    
    private final ClientConsumer receiver;
    private final ChannelAwareSink<ClientMessage, T> sink;
    private final Class<T> messageType;

    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data fetched from the queue.
     * @param messageType the class of the message data the consumer accepts; 
     * needed for deserialization.
     * @throws HornetQException if an error occurs while setting up HornetQ to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
                       ChannelSink<T> consumer, Class<T> messageType) 
                    throws HornetQException {
        this(queue, (meta, data) -> consumer.consume(data), messageType);
        requireNonNull(consumer, "consumer");
    }
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data and metadata fetched from the queue.
     * @param messageType the class of the message data the consumer accepts; 
     * needed for deserialization.
     * @throws HornetQException if an error occurs while setting up HornetQ to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
            ChannelAwareSink<ClientMessage, T> consumer, Class<T> messageType) 
         throws HornetQException {
        requireNonNull(queue, "queue");
        requireNonNull(consumer, "consumer");
        requireNonNull(messageType, "messageType");
        
        this.sink = consumer;
        this.messageType = messageType;
        this.receiver = queue.newConsumer();
        this.receiver.setMessageHandler(this);
    }
    
    private void removeFromQueue(ClientMessage msg) {
        try {
            msg.acknowledge();
        } catch (HornetQException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        T messageData = readBody(msg, messageType);
        removeFromQueue(msg);
        sink.consume(Optional.of(msg), messageData);
    }
    
}
