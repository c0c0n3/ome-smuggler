package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.core.types.FutureTimepoint.now;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.newPairs;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.PositiveN;
import util.object.Pair;

public class ReschedulingSinkTest implements Reschedulable<Long> {

    private SourceQueue<CountedSchedule, Long> loopback;
    private ReschedulingSink<Long> target;
    private ArrayList<Pair<CountedSchedule, Long>> argumentsPassedToNext;
    private ArrayList<Optional<Pair<Long, FutureTimepoint>>> valuesReturnedByNext;
    
    @Before
    public void setup() {
        loopback = new SourceQueue<>();
        target = new ReschedulingSink<>(this, loopback);
        argumentsPassedToNext = new ArrayList<>();
        valuesReturnedByNext = new ArrayList<>();
    }
    
    @Override
    public Optional<Pair<Long, FutureTimepoint>> next(
            CountedSchedule current, Long daysToAdd) {
        
        argumentsPassedToNext.add(pair(current, daysToAdd));
        
        Optional<Pair<Long, FutureTimepoint>> returnValue;
        if (daysToAdd == 0) {    
            returnValue = Optional.empty();
        }
        else {
            Duration newTimepoint = current.when().get().plusDays(daysToAdd);
            FutureTimepoint when = new FutureTimepoint(newTimepoint);
            Long expectedNextCount = current.count().get() + 1;
            returnValue = Optional.of(pair(expectedNextCount, when));
        }
        valuesReturnedByNext.add(returnValue);
        
        return returnValue;
    }
    
    @SuppressWarnings("unchecked")
    private Pair<CountedSchedule, Long>[] expectedMetadataAndDataSentOnChannel() {
        return valuesReturnedByNext
              .stream()
              .map(Optional::get)
              .map(v -> pair(new CountedSchedule(v.snd(), PositiveN.of(v.fst())), 
                             v.fst()))
              .toArray(Pair[]::new);
    }
    
    private Pair<CountedSchedule, Long>[] actualMetadataAndDataSentOnChannel() {
        return loopback.dequeue().toArray(newPairs(0));
    }
    
    @Test
    public void firstCallToConsume() {
        target.consume(message(1L));
        
        assertArrayEquals(expectedMetadataAndDataSentOnChannel(), 
                          actualMetadataAndDataSentOnChannel());
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg1Null() {
        new ReschedulingSink<>(null, loopback);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg2Null() {
        new ReschedulingSink<>((s, d) -> Optional.of(pair("", now())), null);
    }
    
}
