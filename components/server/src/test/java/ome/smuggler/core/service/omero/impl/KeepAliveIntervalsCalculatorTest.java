package ome.smuggler.core.service.omero.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.service.omero.impl.KeepAliveIntervalsCalculator.intervals;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.time.Duration;

@RunWith(Theories.class)
public class KeepAliveIntervalsCalculatorTest {

    // length / unit;  // = q where l = q*u + r (Euclidean division)
    @DataPoints
    public static long[] intervalsDurationSupply = new long[] { 5, 10, 11, 19 };

    @DataPoints
    public static short[] reminderSupply = new short[] { 0, 1, 2, 3, 4 };

    @DataPoints
    public static int[] quotientSupply = new int[] { 1, 2, 3, 4 };

    private static Duration[] compute(long intervalsDuration,
                                      int quotient,
                                      short reminder) {
        Duration howLong = Duration.ofMillis(
                                quotient * intervalsDuration + reminder);
        return intervals(howLong, Duration.ofMillis(intervalsDuration))
                .toArray(Duration[]::new);
    }

    @Theory
    public void allIntervalsHaveSameDuration(long intervalsDuration,
                                             int quotient,
                                             short reminder) {
        Duration[] actual = compute(intervalsDuration, quotient, reminder);
        for (Duration d: actual) {
            assertThat(d.toMillis(), is(intervalsDuration));
        }
    }

    @Theory
    public void produceMaxNumberOfIntervals(long intervalsDuration,
                                            int quotient,
                                            short reminder) {
        Duration[] actual = compute(intervalsDuration, quotient, reminder);
        assertThat(actual.length, is(quotient));
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullHowLong() {
        intervals(null, Duration.ofDays(1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwIfNegativeHowLong() {
        intervals(Duration.ofMillis(-1), Duration.ofDays(1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwIfZeroHowLong() {
        intervals(Duration.ZERO, Duration.ofDays(1));
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullIntervalsDuration() {
        intervals(Duration.ofDays(1), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwIfNegativeIntervalsDuration() {
        intervals(Duration.ofDays(1), Duration.ofMillis(-1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwIfZeroIntervalsDuration() {
        intervals(Duration.ofDays(1), Duration.ZERO);
    }

}
