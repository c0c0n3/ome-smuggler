package integration.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.Optional;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OptionalIntegerTest extends JsonWriteReadTest<Optional<Integer>> {
    
    @Test
    @SuppressWarnings("unchecked")
    public void deserializesDoubleDueToTypeErasure() throws Exception {
        Optional<Integer> initialValue = Optional.of(1);
        Class<Optional<Integer>> valueType = (Class<Optional<Integer>>) 
                                                initialValue.getClass(); 
        Optional<Integer> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(not(initialValue)));
        assertThat(readValue, is(Optional.of(1.0)));  // (*)
    }
    /* (*) Type erasure goodness!
     * So what's happening here? 
     * 
     * 1. Gson serializes initialValue to: { value: 1 }
     * 2. Gson deserializes it to Optional<Double> as it cannot know we want 1 
     *    to be an Integer.
     * 3. So the actual type of readValue.value is Double; the fact we declared
     *    it as Optional<Integer> makes no difference due to type erasure. 
     * 
     * What's not to like?
     * Fortunately Gson has a workaround for this, see below.
     */
    
    @Test
    public void jsonSerializeAndDeserialize() throws Exception {
        Gson mapper = new Gson();
        
        Optional<Integer> initialValue = Optional.of(1);
        String serialized = mapper.toJson(initialValue);
        
        Type valueType = new TypeToken<Optional<Integer>>(){}.getType();
        Optional<Integer> readValue = mapper.fromJson(serialized, valueType);
        
        assertThat(readValue, is(initialValue));
    }
    
}
