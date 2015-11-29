package ome.smuggler.core.msg;

import java.time.Duration;

/**
 * A channel source that allows the scheduling of messages to send. 
 */
public interface SchedulingSource<T> 
    extends ConfigurableChannelSource<Duration, T> {

    /**
     * Sends the message so that the channel will only deliver it to consumers
     * at the specified time in the future.
     * @param timeSpanFromNow amount of time from now to specify when in the
     * future the message should be delivered.
     */
    @Override
    void send(Duration timeSpanFromNow, T data) throws Exception;

    /**
     * Sends the message data without scheduling, the message may be consumed
     * any time from now.
     */
    @Override
    void send(T data) throws Exception;

}
