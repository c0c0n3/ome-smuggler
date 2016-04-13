package ome.smuggler.core.msg;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class ChannelMessageTest {

    @Test(expected = NullPointerException.class)
    public void ctor1ThrowsIfArg1Null() {
        new ChannelMessage<>(null, 0);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctor1ThrowsIfArg2Null() {
        new ChannelMessage<>(0, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctor2ThrowsIfArgNull() {
        new ChannelMessage<>(null);
    }
    
    @Test
    public void metadataIsEmptyIfNotSet() {
        Optional<?> actual = new ChannelMessage<>(0).metadata();
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void equalsReturnsFalseOnNullArg() {
        assertFalse(new ChannelMessage<>(0).equals(null));
    }
    
    @Test
    public void equalsReturnsFalseOnDifferentType() {
        assertFalse(new ChannelMessage<>(0).equals(""));
        
        ChannelMessage<Integer, String> m1 = new ChannelMessage<>(0, "");
        ChannelMessage<String, Integer> m2 = new ChannelMessage<>("", 0);
        assertFalse(m1.equals(m2));
    }
    
    @Test
    public void equalsReturnsFalseOnDifferentValues() {
        ChannelMessage<String, Integer> m1 = new ChannelMessage<>(0);
        ChannelMessage<String, Integer> m2 = new ChannelMessage<>("", 0);
        assertFalse(m1.equals(m2));
    }
    
    @Test
    public void equalsReturnsTrueOnEquivalentValues() {
        ChannelMessage<String, Integer> m1 = new ChannelMessage<>("", 0);
        ChannelMessage<String, Integer> m2 = new ChannelMessage<>("", 0);
        assertTrue(m1.equals(m2));
        
        m1 = new ChannelMessage<>(0);
        m2 = new ChannelMessage<>(0);
        assertTrue(m1.equals(m2));
    }
    
}
