package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.core.types.FutureTimepoint.now;
import static ome.smuggler.providers.q.Messages.durableMessage;
import static ome.smuggler.providers.q.Messages.setScheduledDeliveryTime;

import java.io.OutputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.types.FutureTimepoint;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future.
 */
public class ScheduleTask<T> implements SchedulingSource<T> {

    private final EnqueueTask<T> channel;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @throws ActiveMQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ScheduleTask(QueueConnector queue,
                        SinkWriter<T, OutputStream> serializer)
            throws ActiveMQException {
        this.channel = new EnqueueTask<>(queue, serializer);
    }
    
    /**
     * Sends the message so that the channel will only deliver it to consumers
     * at the specified time in the future.
     * @param msg amount of time from now to specify when in the
     * future the message should be delivered.
     */
    @Override
    public void send(ChannelMessage<FutureTimepoint, T> msg) throws Exception {
        requireNonNull(msg, "msg");
        
        FutureTimepoint when = msg.metadata().orElse(now());
        channel.send(
                message(durableMessage().andThen(
                            setScheduledDeliveryTime(when)), 
                        msg.data()));
    }
    
}
