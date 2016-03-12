package integration.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.providers.json.JsonSinkWriter;
import ome.smuggler.providers.json.JsonSourceReader;

public class JsonWriteReadTest {

    protected String serializedData;

    protected <T> void write(T valueToWrite) {
        StringWriter sink = new StringWriter();
        SinkWriter<T> writer = new JsonSinkWriter<>(sink);
        
        writer.uncheckedWrite(valueToWrite);
        serializedData = sink.toString();
    }
    
    protected <T> T read(Class<T> valueType) {
        StringReader source = new StringReader(serializedData);
        SourceReader<T> reader = new JsonSourceReader<>(valueType, source);
        
        return reader.uncheckedRead();
    }
    
    protected <T> T read(TypeToken<T> valueType) {
        StringReader source = new StringReader(serializedData);
        SourceReader<T> reader = new JsonSourceReader<>(valueType, source);
        
        return reader.uncheckedRead();
    }
    
    protected <T> T writeThenRead(T valueToWrite, Class<T> valueType) {
        write(valueToWrite);
        return read(valueType);
    }
    
    protected <T> T writeThenRead(T valueToWrite, TypeToken<T> valueType) {
        write(valueToWrite);
        return read(valueType);
    }
    
    protected <T> void assertWriteThenReadGivesInitialValue(
            T initialValue, Class<T> valueType) {
        T readValue = writeThenRead(initialValue, valueType);
        assertThat(readValue, is(initialValue));
    }
    
    protected <T> void assertWriteThenReadGivesInitialValue(
            T initialValue, TypeToken<T> valueType) {
        T readValue = writeThenRead(initialValue, valueType);
        assertThat(readValue, is(initialValue));
    }
    
}
