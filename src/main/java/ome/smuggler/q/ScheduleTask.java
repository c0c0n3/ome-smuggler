package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.Messages.durableMessage;
import static ome.smuggler.q.Messages.setScheduledDeliveryTime;

import org.hornetq.api.core.HornetQException;

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
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ScheduleTask(QueueConnector queue) throws HornetQException {
        this.channel = new EnqueueTask<>(queue);
    }
    
    /**
     * Sends the message so that the channel will only deliver it to consumers
     * at the specified time in the future.
     * @param timeSpanFromNow amount of time from now to specify when in the
     * future the message should be delivered.
     */
    @Override
    public void send(FutureTimepoint when, T data) throws Exception {
        requireNonNull(when, "when");
        channel.send(durableMessage().andThen(
                        setScheduledDeliveryTime(when)), 
                     data);
    }
    
}
