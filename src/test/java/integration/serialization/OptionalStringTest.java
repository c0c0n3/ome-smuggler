package integration.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class OptionalStringTest extends JsonWriteReadTest<Optional<String>> {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Optional<String> initialValue = Optional.of("xxx");
        Class<Optional<String>> valueType = (Class<Optional<String>>) 
                                                initialValue.getClass(); 
        Optional<String> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(initialValue));
    }
    
}
