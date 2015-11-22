package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.MessageBody.readBody;
import static util.error.Exceptions.throwAsIfUnchecked;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.core.config.CoreQueueConfiguration;

import ome.smuggler.core.msg.ChannelSink;

/**
 * Receives messages asynchronously from a queue and dispatches them to a 
 * consumer.
 */
public class DequeueTask<T> implements MessageHandler {
    
    private final ClientConsumer receiver;
    private final ChannelSink<T> sink;
    private final Class<T> messageType;

    /**
     * Creates a new instance.
     * @param config the queue to receive messages from.
     * @param session the session to use.
     * @param consumer consumes messages fetched from the queue.
     * @param messageType the class of the message the consumer accepts; needed
     * for deserialization.
     * @throws HornetQException if an error occurs while setting up HornetQ to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(CoreQueueConfiguration config, ClientSession session,
                       ChannelSink<T> consumer, Class<T> messageType) 
                    throws HornetQException {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        requireNonNull(consumer, "consumer");
        requireNonNull(messageType, "messageType");
        
        this.sink = consumer;
        this.messageType = messageType;
        this.receiver = session.createConsumer(config.getName(), false);
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
        sink.consume(messageData);
    }
    
}
