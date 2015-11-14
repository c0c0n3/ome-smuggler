package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.positiveInt;
import ome.smuggler.core.types.PositiveN;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class PositiveNTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        PositiveN initialValue = positiveInt("1").getRight();
        Class<PositiveN> valueType = (Class<PositiveN>) initialValue.getClass();
        TypeToken<PositiveN> typeToken = new TypeToken<PositiveN>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}
