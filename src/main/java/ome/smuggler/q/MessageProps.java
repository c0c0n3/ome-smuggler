package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.time.Duration;
import java.util.function.Function;

import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientMessage;

import ome.smuggler.core.types.FutureTimepoint;

public class MessageProps {

    public static final String RetryCountKey = "ome.smuggler.q.RetryCount";

    public static Function<QueueConnector, ClientMessage> durableMessage() {
        return QueueConnector::newDurableMessage;
    }
    
    public static Function<ClientMessage, ClientMessage> setProp(
            String key, long value) {
        requireString(key, "key");
        return m -> (ClientMessage) m.putLongProperty(key, value);
    }

    public static Function<ClientMessage, ClientMessage> setScheduledDeliveryTime(
            Duration timeSpanFromNow) {
        FutureTimepoint when = new FutureTimepoint(timeSpanFromNow);
        return setProp(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), 
                       when.get().toMillis());
    }
    
    public static Function<ClientMessage, ClientMessage> setRetryCount(long count) {
        return setProp(RetryCountKey, count);
    }
    
    public static long getRetryCount(ClientMessage msg) {
        requireNonNull(msg, "msg");
        return msg.getLongProperty(RetryCountKey);
    }
    
}
