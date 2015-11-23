package ome.smuggler.q;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientMessage;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future.
 */
public class ScheduleTask<T> extends EnqueueTask<T> {

    private final Duration timeSpanFromNow;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages. 
     * @param timeSpanFromNow amount of time from now to specify when in the
     * future the message should be delivered.
     * @throws HornetQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ScheduleTask(QueueConnector queue, Duration timeSpanFromNow) 
            throws HornetQException {
        super(queue);
        requireNonNull(timeSpanFromNow, "timeSpanFromNow");
        
        this.timeSpanFromNow = timeSpanFromNow;
    }
    
    private long millisFromNow() {
        long now = System.currentTimeMillis();
        return now + timeSpanFromNow.toMillis();
    }
    
    @Override
    protected ClientMessage newMessage(QueueConnector queue) {
        ClientMessage msg = queue.newDurableMessage();
        msg.putLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), 
                            millisFromNow());
        return msg;
    }
    
}
