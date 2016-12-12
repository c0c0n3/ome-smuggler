package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import ome.smuggler.core.types.PositiveN;

/**
 * Utility to compute OMERO session keep-alive intervals.
 */
public class KeepAliveIntervalsCalculator {

    private static long toMillis(Duration d) {
        return PositiveN.of(d.toMillis()).get();  // throws if not positive
    }

    private static long numberOfIntervals(Duration howLong,
                                          Duration intervalDuration) {
        long unit = toMillis(intervalDuration);
        long length = toMillis(howLong);

        return length / unit;  // = q where l = q*u + r (Euclidean division)
    }

    /**
     * Computes how many constant-duration intervals are needed to add up to
     * an amount of time that is the closest to a given desired period of time
     * without exceeding it.
     * @param howLong the period of time to split into intervals.
     * @param intervalDuration how long each interval should last.
     * @return a stream containing the maximum possible number of interval
     * durations that when added yield an amount of time at most equal to
     * the specified period of time.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if any duration is not positive.
     */
    public static Stream<Duration> intervals(Duration howLong,
                                             Duration intervalDuration) {
        requireNonNull(howLong, "howLong");
        requireNonNull(intervalDuration, "intervalDuration");

        long n = numberOfIntervals(howLong, intervalDuration);
        return LongStream.range(0, n)
                         .mapToObj(i -> intervalDuration);
    }

}
