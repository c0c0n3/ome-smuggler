package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.q.Messages.durableMessage;
import static ome.smuggler.q.Messages.setScheduleCount;
import static ome.smuggler.q.Messages.setScheduledDeliveryTime;

import org.hornetq.api.core.HornetQException;

import ome.smuggler.core.msg.ConfigurableChannelSource;
import ome.smuggler.core.msg.CountedSchedule;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future and makes a sender-specified delivery count available in
 * the metadata.
 */
public class CountedScheduleTask<T> 
    implements ConfigurableChannelSource<CountedSchedule, T> {

    private final EnqueueTask<T> channel;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages. 
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CountedScheduleTask(QueueConnector queue) throws HornetQException {
        this.channel = new EnqueueTask<>(queue);
    }
    
    @Override
    public void send(T data) throws Exception {
        channel.send(data);
    }

    @Override
    public void send(CountedSchedule metadata, T data) throws Exception {
        requireNonNull(metadata, "metadata");
        channel.send(durableMessage().andThen(
                     setScheduledDeliveryTime(metadata.when())).andThen(
                     setScheduleCount(metadata.count().get())), 
                     data);
    }

}
