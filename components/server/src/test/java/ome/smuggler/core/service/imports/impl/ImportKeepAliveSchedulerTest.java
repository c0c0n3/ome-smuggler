package ome.smuggler.core.service.imports.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static ome.smuggler.core.types.ImportKeepAlive.keepAliveMessage;
import static ome.smuggler.core.types.ValueParserFactory.email;
import static ome.smuggler.core.types.ValueParserFactory.uri;

import java.time.Duration;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportConfigReader;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.core.types.Schedule;
import util.sequence.Arrayz;

@RunWith(Theories.class)
public class ImportKeepAliveSchedulerTest {

    public static final Duration interval = ImportConfigReader.DefaultKeepAliveInterval;
    
    public static QueuedImport newImportRequest() {
        ImportInput data = new ImportInput(
                                    email("user@micro.edu").getRight(), 
                                    uri("target/file").getRight(), 
                                    uri("omero:1234").getRight(), 
                                    "sessionKey");
        return new QueuedImport(new ImportId(), data);
    }
    
    @DataPoints
    public static Boolean[][] seedsSupply = new Boolean[][] {
        array(false), array(true),  // true = stop, false = keep alive 
        array(false, false), array(false, true), array(true, false), array(true, true),
        array(false, false, true), array(false, true, false),
        array(false, false, true, false)
    };
    
    public static ImportKeepAlive[] generateMessageStream(Boolean[] seeds) {
        QueuedImport request = newImportRequest();
        return Arrayz.op(ImportKeepAlive[]::new).map(
                (i, stop) -> new ImportKeepAlive(request, stop), 
                seeds);
    }
    
    public static void assertTimepoint(FutureTimepoint actual, 
                                       FutureTimepoint expected) {
        Duration delta = actual.get().minus(expected.get()).abs();
        Duration tolerableDelta = Duration.ofSeconds(1);
        assertThat(delta, lessThanOrEqualTo(tolerableDelta));
    }
    
    
    private ImportKeepAliveScheduler target;
    
    @Before
    public void setup() {
        target = new ImportKeepAliveScheduler(interval);
    }
    
    @Theory
    public void stopSchedulingOnFirstStopMessage(Boolean[] seeds) {
        ImportKeepAlive[] ms = generateMessageStream(seeds);
        CountedSchedule current = CountedSchedule.first();
        Optional<Schedule<ImportKeepAlive>> actual = null;
        int k = 0;
        for (; k < seeds.length; ++k) {
            if (ms[k].stop()) break;
            
            actual = target.nextSchedule(current, ms[k]);
            
            assertNotNull(actual);
            assertTrue("expected schedule " + k, actual.isPresent());
            
            FutureTimepoint expectedSchedule = new FutureTimepoint(interval);
            assertTimepoint(actual.get().when(), expectedSchedule);
            
            current = current.next(actual.get().when());
        }
        for (; k < seeds.length; ++k) {
            current = current.next(FutureTimepoint.now());
            actual = target.nextSchedule(current, ms[k]);
            
            assertNotNull(actual);
            assertFalse("not expected schedule " + k, actual.isPresent());
        }
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new ImportKeepAliveScheduler(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void nextThrowsIfArg1Null() {
        target.nextSchedule(null, keepAliveMessage(newImportRequest()));
    }
    
    @Test (expected = NullPointerException.class)
    public void nextThrowsIfArg2Null() {
        target.nextSchedule(CountedSchedule.first(), null);
    }
    
}
