package integration.serialization;

import static ome.smuggler.core.types.ImportKeepAlive.keepAliveMessage;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.core.types.QueuedImport;

public class ImportKeepAliveTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        QueuedImport importRequest = 
                new QueuedImport(new ImportId(), ImportInputTest.makeNew());
        ImportKeepAlive initialValue = keepAliveMessage(importRequest);
        Class<ImportKeepAlive> valueType = (Class<ImportKeepAlive>) 
                                        initialValue.getClass();
        TypeToken<ImportKeepAlive> typeToken = new TypeToken<ImportKeepAlive>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}