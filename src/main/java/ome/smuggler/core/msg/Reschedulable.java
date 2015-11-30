package ome.smuggler.core.msg;

import java.util.Optional;

import ome.smuggler.core.types.FutureTimepoint;
import util.object.Pair;

/**
 * A task that consumes a channel sink's output and optionally requests the 
 * channel to deliver again some more data at a later point in time.
 * @see ReschedulingSink 
 */
public interface Reschedulable<T> {

    /**
     * Consumes the output of the channel this instance is bound to.
     * @param current information about this invocation's schedule.
     * @param data the data received from the channel.
     * @return when and what data to input again in the channel or empty if no
     * further action is required. Never return {@code null}.
     */
    Optional<Pair<T, FutureTimepoint>> next(CountedSchedule current, T data);
   
}
