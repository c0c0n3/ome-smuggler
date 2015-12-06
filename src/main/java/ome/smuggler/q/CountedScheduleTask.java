package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.q.Messages.durableMessage;
import static ome.smuggler.q.Messages.setScheduleCount;
import static ome.smuggler.q.Messages.setScheduledDeliveryTime;

import org.hornetq.api.core.HornetQException;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSource;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future and makes a sender-specified delivery count available in
 * the metadata.
 * @see CountedScheduleSink
 */
public class CountedScheduleTask<T> implements MessageSource<CountedSchedule, T> {

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
    public void send(ChannelMessage<CountedSchedule, T> msg) throws Exception {
        requireNonNull(msg, "msg");
        
        CountedSchedule metadata = msg.metadata()
                                      .orElse(CountedSchedule.first());
        channel.send(
                message(durableMessage().andThen(
                             setScheduledDeliveryTime(metadata.when())).andThen(
                             setScheduleCount(metadata.count())), 
                        msg.data()));
    }

}
