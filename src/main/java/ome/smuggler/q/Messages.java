package ome.smuggler.q;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.function.Function;

import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientMessage;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.types.FutureTimepoint;

public class Messages {

    public static final String ScheduleCountKey = 
            CountedSchedule.class.getName() + "#count";

    public static Function<QueueConnector, ClientMessage> durableMessage() {
        return QueueConnector::newDurableMessage;
    }
    
    public static Function<ClientMessage, ClientMessage> setProp(
            String key, long value) {
        requireString(key, "key");
        return m -> (ClientMessage) m.putLongProperty(key, value);
    }

    public static Function<ClientMessage, ClientMessage> setScheduledDeliveryTime(
            FutureTimepoint when) {
        return setProp(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), 
                       when.get().toMillis());
    }
    
    public static Function<ClientMessage, ClientMessage> setScheduleCount(long count) {
        return setProp(ScheduleCountKey, count);
    }
    
    public static long getScheduleCount(ClientMessage msg) {
        requireNonNull(msg, "msg");
        return msg.getLongProperty(ScheduleCountKey);
    }
    
}
