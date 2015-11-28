package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import util.object.Wrapper;

/**
 * A time point in the future with respect to the time at which it was 
 * instantiated.
 */
public class FutureTimepoint extends Wrapper<Duration> {

    private static void check(Duration timeSpanFromNow) {
        requireNonNull(timeSpanFromNow, "timeSpanFromNow");
        if (timeSpanFromNow.isNegative()) {
            throw new IllegalArgumentException("negative time span");
        }
    }
    
    private static Duration timepoint(Duration timeSpanFromNow) {
        check(timeSpanFromNow);
        
        long now = System.currentTimeMillis();
        return timeSpanFromNow.plusMillis(now);
    }
    
    /**
     * Creates a new instance.
     * @param timeSpanFromNow an amount of time to add to the current time to
     * get a time point in the future.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the argument is a negative duration.
     */
    public FutureTimepoint(Duration timeSpanFromNow) {
        super(timepoint(timeSpanFromNow));
    }

}
