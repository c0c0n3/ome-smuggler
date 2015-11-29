package ome.smuggler.q;

import static ome.smuggler.q.MessageProps.durableMessage;
import static ome.smuggler.q.MessageProps.setScheduledDeliveryTime;

import java.time.Duration;

import org.hornetq.api.core.HornetQException;

import ome.smuggler.core.msg.ConfigurableChannelSource;
import ome.smuggler.core.msg.SchedulingSource;

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
    public void send(Duration timeSpanFromNow, T data) throws Exception {
        channel.send(durableMessage().andThen(
                        setScheduledDeliveryTime(timeSpanFromNow)), 
                     data);
    }

    /**
     * Sends the message data without scheduling, the message may be consumed
     * any time from now.
     */
    @Override
    public void send(T data) throws Exception {
        channel.send(data);
    }
    
}
