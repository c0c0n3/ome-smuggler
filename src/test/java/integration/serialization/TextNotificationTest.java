package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.TextNotification;

public class TextNotificationTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Email recipient = email("guy@fbi.edu").getRight();
        TextNotification initialValue = 
                new TextNotification(recipient, "title", "content"); 
        Class<TextNotification> valueType = (Class<TextNotification>) 
                initialValue.getClass();
        TypeToken<TextNotification> typeToken = 
                new TypeToken<TextNotification>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}
