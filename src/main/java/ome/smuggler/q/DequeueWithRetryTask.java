package ome.smuggler.q;

import static ome.smuggler.q.MessageBody.readBody;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;

import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.RetryLater;

public class DequeueWithRetryTask<T> extends DequeueTask<T> {

    private final ChannelSource<T> loopback;
    
    public DequeueWithRetryTask(QueueConnector queue, ChannelSink<T> consumer, 
            Class<T> messageType)
            throws HornetQException {
        super(queue, consumer, messageType);
        loopback = new EnqueueTask<>(queue);
    }
    
    private void putBackOnQueue(ClientMessage msg) {
        
    }
    
    private boolean attemptConsume(ClientMessage msg) {
        T messageData = readBody(msg, messageType);
        try {
            sink.consume(messageData);
            return true;
        } catch (RetryLater e) {
            return false;
        }
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        
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
 */