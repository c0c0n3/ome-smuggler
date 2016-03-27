package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.providers.q.MessageBody.readBody;
import static util.error.Exceptions.throwAsIfUnchecked;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.MessageHandler;

import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.MessageSink;

/**
 * Receives messages asynchronously from a queue and dispatches them to a 
 * consumer.
 */
public class DequeueTask<T> implements MessageHandler {
    
    private final ClientConsumer receiver;
    private final MessageSink<ClientMessage, T> sink;
    private final Class<T> messageType;
    private final boolean redeliverOnCrash;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data fetched from the queue.
     * @param messageType the class of the message data the consumer accepts; 
     * needed for deserialization.
     * @param redeliverOnCrash if {@code true} and the process terminates 
     * abnormally (e.g. segfault, power failure) while the consumer is busy 
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be 
     * delivered once to the consumer.
     * @throws HornetQException if an error occurs while setting up HornetQ to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
                       ChannelSink<T> consumer, Class<T> messageType,
                       boolean redeliverOnCrash) 
                    throws HornetQException {
        this(queue, MessageSink.forwardDataTo(consumer), messageType, 
                redeliverOnCrash);
    }
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data and metadata fetched from the queue.
     * @param messageType the class of the message data the consumer accepts; 
     * needed for deserialization.
     * @param redeliverOnCrash if {@code true} and the process terminates 
     * abnormally (e.g. segfault, power failure) while the consumer is busy 
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be 
     * delivered once to the consumer.
     * @throws HornetQException if an error occurs while setting up HornetQ to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
            MessageSink<ClientMessage, T> consumer, Class<T> messageType,
            boolean redeliverOnCrash) throws HornetQException {
        requireNonNull(queue, "queue");
        requireNonNull(consumer, "consumer");
        requireNonNull(messageType, "messageType");
        
        this.sink = consumer;
        this.messageType = messageType;
        this.receiver = queue.newConsumer();
        this.receiver.setMessageHandler(this);
        this.redeliverOnCrash = redeliverOnCrash;
    }
    
    private void removeFromQueue(ClientMessage msg) {
        try {
            msg.acknowledge();
        } catch (HornetQException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    private void consumeThenRemove(ClientMessage msg, T messageData) {
        try {
            sink.consume(message(msg, messageData));  // (*)
        } finally {
            removeFromQueue(msg);
        }
    }
    /* NOTE. If the process dies here, the message is still in the queue as it
     * hasn't been acknowledged yet. HornetQ will deliver it again on reboot.
     * See:
     * - http://stackoverflow.com/questions/15243991/what-happen-if-client-acknowledgment-not-done
     */
    
    private void removeThenConsume(ClientMessage msg, T messageData) {
        removeFromQueue(msg);
        sink.consume(message(msg, messageData));
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        T messageData = readBody(msg, messageType);
        if (redeliverOnCrash) {
            consumeThenRemove(msg, messageData);
        } else {
            removeThenConsume(msg, messageData);
        }
    }
    
}
