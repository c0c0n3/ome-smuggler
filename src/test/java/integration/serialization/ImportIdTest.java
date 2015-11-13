package integration.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import ome.smuggler.core.types.ImportId;

import org.junit.Test;

public class ImportIdTest extends JsonWriteReadTest<ImportId> {
    
    @Test
    public void jsonSerializeAndDeserialize() throws Exception {
        ImportId initialValue = new ImportId();
        ImportId readValue = writeThenRead(initialValue, ImportId.class);
        
        assertThat(readValue, is(initialValue));
    }
    
}
