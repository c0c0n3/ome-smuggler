package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import util.object.Pair;

public class SourceQueueTest {

    private SourceQueue<String, Integer> queue;
    
    @Before
    public void setup() {
        queue = new SourceQueue<>();
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfNullData() throws Exception {
        queue.send(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfNullMetadata() throws Exception {
        queue.send(null, 0);
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfHasMetadataButNullData() throws Exception {
        queue.send("", null);
    }
    
    @Test
    public void headReturnsEmptyIfQueueIsEmpty() {
        Optional<Pair<Optional<String>, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void headDataReturnsEmptyIfQueueIsEmpty() {
        Optional<Integer> actual = queue.headData();
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void headReturnsEmptyMetaIfNoMetaWasSent() throws Exception {
        Integer data = 1;
        queue.send(data);
        Optional<Pair<Optional<String>, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        Pair<Optional<String>, Integer> msg = actual.get();
        Optional<String> metadata = msg.fst();
        
        assertNotNull(metadata);
        assertFalse(metadata.isPresent());
        assertThat(msg.snd(), is(data));
    }
    
    @Test
    public void headReturnsMetaIfMetaWasSent() throws Exception {
        String metadata = "m";
        Integer data = 1;
        queue.send(metadata, data);
        Optional<Pair<Optional<String>, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        Pair<Optional<String>, Integer> msg = actual.get();
        
        assertNotNull(msg.fst());
        assertTrue(msg.fst().isPresent());
        assertThat(msg.fst().get(), is(metadata));
        assertThat(msg.snd(), is(data));
    }
    
    @Test
    public void queueMessages() throws Exception {
        String m1 = "m1", m2 = "m2";
        Integer d1 = 1, d2 = 2;
        queue.send(m1, d1);
        queue.send(m2, d2);
        
        Pair<Optional<String>, Integer> msg = queue.head().get();
        assertThat(msg.fst().get(), is(m1));
        assertThat(msg.snd(), is(d1));
        
        msg = queue.head().get();
        assertThat(msg.fst().get(), is(m2));
        assertThat(msg.snd(), is(d2));
        
        assertFalse(queue.head().isPresent());
    }
    
    @Test
    public void dequeueAllReturnsEmptyIfQueueIsEmpty() {
        Stream<Pair<Optional<String>, Integer>> actual = queue.dequeue();
        
        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void dequeueData() throws Exception {
        Integer[] data = { 1,  2 };
        queue.send(data[0]);
        queue.send(data[1]);
        Integer[] actual = queue.dequeueData().toArray(Integer[]::new);
        
        assertArrayEquals(data, actual);
    }
    
}
