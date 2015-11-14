package integration.serialization;

import java.util.Optional;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class OptionalStringTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Optional<String> initialValue = Optional.of("xxx");
        Class<Optional<String>> valueType = (Class<Optional<String>>) 
                                                initialValue.getClass();
        TypeToken<Optional<String>> typeToken = new TypeToken<Optional<String>>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}
