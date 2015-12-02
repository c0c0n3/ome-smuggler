package ome.smuggler.q;

import static util.sequence.Arrayz.hasNulls;

import java.time.Duration;
import java.util.Optional;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;

import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.RetryLater;

public class DequeueWithRetryTask<T> extends DequeueTask<T> {

    public static final String RetryCount = "ome.smuggler.q.RetryCount";
    
    private final Duration[] delays;
    private final ChannelSource<T> loopback;
    
    
    public DequeueWithRetryTask(QueueConnector queue, ChannelSink<T> consumer, 
            Class<T> messageType, Duration[] delays)
            throws HornetQException {
        super(queue, consumer, messageType);
        if (delays == null || hasNulls(delays)) {  // zero len is okay tho
            throw new NullPointerException("array is null or contains nulls");
        }
        
        this.delays = delays;
        loopback = null;//new EnqueueTask<>(queue);
    }
    
    private int getRetryCount(ClientMessage msg) {
        return msg.containsProperty(RetryCount) ? 
                msg.getIntProperty(RetryCount) : 0;
    }
    
    private Optional<Duration> nextDelay(int retryCount) {
        return 0 <= retryCount && retryCount < delays.length ?
                Optional.of(delays[retryCount]) : Optional.empty();
    }
    
    private void putBackOnQueue(ClientMessage msg) {
        
    }
    
    private boolean attemptConsume(ClientMessage msg) {
        try {
            super.onMessage(msg);
            return true;
        } catch (RetryLater e) {
            return false;
        }
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        boolean consumed = attemptConsume(msg);
        if (!consumed) {
            putBackOnQueue(msg);
        }
    }
    
}
/* NOTE. Why do this when HornetQ supports delayed re-delivery?!
 * Before you send WTH's flying all over, know that at least I tried it, 
 * but there was some dodginess I couldn't quite figure out, eventually
 * ran out of debug cycles I could use on this, so decided to implement
 * re-delivery myself.
 * 
 * For the record, this is what I initially attempted:
 * 
 * 1. Change ServerConnector to create a transacted session.
 * 2. EnqueueTask commits the session after sending the message.
 * 3. DequeueTask rolls back if an the consumer says so.
 * 
 * The code was basically lifted from the HornetQ "delayed re-delivery" and
 * and "transactional" examples. It all seemed to work fine, except for the
 * fact that rolling back was taking ages (i.e. calling the session's rollback
 * method within onMessage) and HornetQ kept on telling me there was something
 * wrong:
 * 
 * + HQ212002: Timed out waiting for handler to complete processing
 * 
 * Okay, now it's my turn for the WTH...
 * 
 * Another thing I didn't have time to test is what messages are rolled back.
 * What happens if producers and consumers share the *same* session---i.e. our
 * set up? Say messages m1 and m2 are on the queue, m1 is delivered to a 
 * consumer which rolls back while m2 still sits on the queue. What is the fate
 * of m2?
 */