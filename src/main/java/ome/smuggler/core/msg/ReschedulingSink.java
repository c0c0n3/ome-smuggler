package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;

import java.util.Optional;

import ome.smuggler.core.types.FutureTimepoint;

/**
 * A channel sink feeding the channel output to a task that may produce more 
 * input to be delivered on the channel at a later point in time.
 */
public class ReschedulingSink<T> 
    implements ChannelAwareSink<CountedSchedule, T> {

    private final Reschedulable<T> task;
    private final MessageSource<CountedSchedule, T> loopback;
    
    /**
     * Creates a new instance.
     * @param task consumes the channel output and optionally produces more
     * data to input into the channel with an indication of when the data 
     * should be delivered to channel's sinks.
     * @param loopback where to put data (if any) produced by the task; normally
     * this would be the same channel this sink is bound to, i.e. the same
     * channel from which the data was received.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ReschedulingSink(Reschedulable<T> task,
                            MessageSource<CountedSchedule, T> loopback) {
        requireNonNull(loopback, "loopback");
        requireNonNull(task, "task");
        
        this.loopback = loopback;
        this.task = task;
    }
    
    private Void reschedule(T data, FutureTimepoint when, CountedSchedule last) {
        CountedSchedule next = last.next(when);
        loopback.uncheckedSend(message(next, data));
        return null;
    }

    @Override
    public void consume(Optional<CountedSchedule> metadata, T data) {
        CountedSchedule current = metadata.orElse(CountedSchedule.first());
        task.next(current, data)
            .map(p -> reschedule(p.fst(), p.snd(), current));
    }
    
}
