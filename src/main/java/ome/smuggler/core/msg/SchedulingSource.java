package ome.smuggler.core.msg;

import java.time.Duration;

import ome.smuggler.core.types.FutureTimepoint;

/**
 * A channel source that allows the scheduling of messages to send. 
 */
public interface SchedulingSource<T> 
    extends ConfigurableChannelSource<FutureTimepoint, T> {

    /**
     * Sends the message so that the channel will only deliver it to consumers
     * at the specified time in the future.
     * @param when future time-point at which the message should be delivered.
     */
    @Override
    void send(FutureTimepoint when, T data) throws Exception;

    /**
     * Sends the message data without scheduling, the message may be consumed
     * any time from now.
     */
    @Override
    default void send(T data) throws Exception {
        FutureTimepoint now = new FutureTimepoint(Duration.ZERO);
        send(now, data);
    }

}
