package ome.smuggler.core.msg;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import static ome.smuggler.core.msg.RetryAction.Retry;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.Schedule;

public class DeliveryRetry<M, D> implements Reschedulable<ChannelMessage<M, D>> {

    private Function<ChannelMessage<M, D>, RetryAction> consumer;
    private Duration[] retryIntervals;

    private Optional<RetryAction> check(RetryAction outcome) {
        return outcome == Retry ? Optional.of(Retry) : Optional.empty();
    }
    
    private Optional<FutureTimepoint> nextRetryTime(CountedSchedule current) {
        int retryCount = current.count().get().intValue() - 1;
        return retryCount < retryIntervals.length ? 
                Optional.of(new FutureTimepoint(retryIntervals[retryCount])) :
                Optional.empty();
    }
    
    @Override
    public Optional<Schedule<ChannelMessage<M, D>>> consume(
            CountedSchedule current, ChannelMessage<M, D> data) {
        RetryAction outcome = consumer.apply(data);
        return check(outcome)
              .flatMap(x -> nextRetryTime(current))
              .map(when -> new Schedule<>(when, data));
    }

}
/* NOTE. Why do this when HornetQ supports delayed re-delivery?!
 * Before you send WTH's flying all over, know that at least I tried it, 
 * but there was some dodginess I couldn't quite figure out, eventually
 * ran out of debug cycles I could use on this, so decided to implement
 * re-delivery myself.
 * 
 * For the record, this is what I initially attempted:
 * 
 * 1. Change ServerConnector to create a transacted session.
 * 2. EnqueueTask commits the session after sending the message.
 * 3. DequeueTask rolls back if an the consumer says so.
 * 
 * The code was basically lifted from the HornetQ "delayed re-delivery" and
 * and "transactional" examples. It all seemed to work fine, except for the
 * fact that rolling back was taking ages (i.e. calling the session's rollback
 * method within onMessage) and HornetQ kept on telling me there was something
 * wrong:
 * 
 * + HQ212002: Timed out waiting for handler to complete processing
 * 
 * Okay, now it's my turn for the WTH...
 * 
 * Another thing I didn't have time to test is what messages are rolled back.
 * What happens if producers and consumers share the *same* session---i.e. our
 * set up? Say messages m1 and m2 are on the queue, m1 is delivered to a 
 * consumer which rolls back while m2 still sits on the queue. What is the fate
 * of m2?
 */