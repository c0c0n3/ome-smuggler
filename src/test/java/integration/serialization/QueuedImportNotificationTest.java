package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.QueuedImportNotification;
import ome.smuggler.core.types.TextNotification;

public class QueuedImportNotificationTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Email recipient = email("guy@fbi.edu").getRight();
        TextNotification outcome = 
                new TextNotification(recipient, "title", "content");
        QueuedImportNotification initialValue = 
                new QueuedImportNotification(new ImportId(), outcome);
        Class<QueuedImportNotification> valueType = (Class<QueuedImportNotification>) 
                initialValue.getClass();
        TypeToken<QueuedImportNotification> typeToken = 
                new TypeToken<QueuedImportNotification>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}
