package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.positiveInt;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import ome.smuggler.core.types.PositiveN;

import org.junit.Test;

public class PositiveNTest extends JsonWriteReadTest<PositiveN> {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        PositiveN initialValue = positiveInt("1").getRight();
        Class<PositiveN> valueType = (Class<PositiveN>) initialValue.getClass(); 
        PositiveN readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(initialValue));
    }
    
}
