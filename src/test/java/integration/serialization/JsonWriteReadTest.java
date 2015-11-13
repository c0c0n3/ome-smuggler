package integration.serialization;

import java.util.function.Consumer;
import java.util.function.Supplier;

import ome.smuggler.core.convert.JsonSinkWriter;
import ome.smuggler.core.convert.JsonSourceReader;
import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;

public class JsonWriteReadTest<T> implements Supplier<String>, Consumer<String> {

    protected String serializedData;

    protected T writeThenRead(T valueToWrite, Class<T> valueType) throws Exception {
        SinkWriter<T> writer = new JsonSinkWriter<>(this);
        SourceReader<T> reader = new JsonSourceReader<>(valueType, this);
        
        writer.write(valueToWrite);
        return reader.read();
    }
    
    @Override
    public void accept(String t) {
        serializedData = t;
    }

    @Override
    public String get() {
        return serializedData;
    }
    
}
