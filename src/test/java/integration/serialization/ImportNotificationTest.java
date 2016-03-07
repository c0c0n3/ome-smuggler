package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportNotification;
import ome.smuggler.core.types.TextNotification;

public class ImportNotificationTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Email recipient = email("guy@fbi.edu").getRight();
        TextNotification outcome = 
                new TextNotification(recipient, "title", "content");
        ImportNotification initialValue = 
                new ImportNotification(new ImportId(), outcome);
        Class<ImportNotification> valueType = (Class<ImportNotification>) 
                initialValue.getClass();
        TypeToken<ImportNotification> typeToken = 
                new TypeToken<ImportNotification>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}
