package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.types.FutureTimepoint.now;

import java.util.Objects;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.PositiveN;

/**
 * Channel metadata to request the scheduling of a message and the sending of
 * a counter to indicate how many times the message has been scheduled for
 * delivery.
 */
public class CountedSchedule {

    /**
     * @return an initial schedule for the current time point with a {@link 
     * #count() counter} initialized to {@code 1}.
     */
    public static CountedSchedule first() {
        return new CountedSchedule(now(), PositiveN.of(1));
    }
    
    private final FutureTimepoint when;
    private final PositiveN count;
    
    /**
     * Creates a new instance.
     * @param when at which future point in time deliver the message.
     * @param count how many times the delivery has been requested.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CountedSchedule(FutureTimepoint when, PositiveN count) {
        requireNonNull(when, "when");
        requireNonNull(count, "count");
        
        this.when = when;
        this.count = count;
    }

    /**
     * @return at which future point in time deliver the message.
     */
    public FutureTimepoint when() {
        return when;
    }
    
    /**
     * @return how many times the delivery has been requested.
     */
    public PositiveN count() {
        return count;
    }
    
    /**
     * Creates a new schedule for the requested time with a counter increased
     * by one with respect to the current {@link #count() counter}. 
     * @param when at which future point in time deliver the message.
     * @return the new schedule.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CountedSchedule next(FutureTimepoint when) {
        return new CountedSchedule(when, PositiveN.of(count().get() + 1));
    }
    
    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        }
        if (x instanceof CountedSchedule) {
            CountedSchedule other = (CountedSchedule) x;
            return Objects.equals(this.when, other.when)
                && Objects.equals(this.count, other.count);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(when, count);
    }
    
}
/* NOTE. Design debt.
 * Need to put more thought into this metadata thingie. Typically it would 
 * relate to channel capabilities, e.g. being able to schedule messages, being
 * able to specify int props for metadata, etc. 
 * Because ConfigurableChannelSource ties a channel implementation to the kind
 * of metadata it supports, it's not possible for senders to accidentally
 * request features not supported by the channel---as it can easily happen
 * with JMS when setting message props. 
 * Now the encoding of this logic in the type system makes the code more robust.
 * (Yay!) But, as it stands now my approach here is not composable, lending
 * itself to tons of boilerplate. (Oh crap!)
 * In fact, consider an implementation that supports both scheduling and setting
 * int props in the metadata. We have three possible usage scenarios: client
 * only needs scheduling, or only long props, or both. Clark the bright spark
 * (erm, me!) decided we need a metadata class for each combination, just like 
 * this one you looking at and a corresponding ConfigurableChannelSource. 
 * As the number of possible combinations grows, surely will also increase the 
 * frequency at which you're cursing me out.
 * A better approach would be to come up with a way to represent channel 
 * capabilities and a way to compose them while still using the type system
 * to make sure senders cannot use a channel that doesn't support the requested
 * features. Applicative functors and monads come to mind. Bingo!
 */