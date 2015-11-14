package ome.smuggler.core.convert;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;

/**
 * Parses an object in JSON serialized form as found in a given data source.
 */
public class JsonSourceReader<T> implements SourceReader<T> {

    private final Gson mapper;
    private final Class<T> valueType;
    private final Supplier<String> rawData;
    
    /**
     * Creates a new instance to read the JSON representation of an object.
     * @param valueType the class of the object to read.
     * @param rawData provides access to the JSON data to deserialize into a
     * {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonSourceReader(Class<T> valueType, Supplier<String> rawData) {
        requireNonNull(valueType, "valueType");
        requireNonNull(rawData, "rawData");
        
        this.mapper = new Gson();
        this.valueType = valueType;
        this.rawData = rawData;
    }
    
    @Override
    public T read() throws JsonParseException, JsonMappingException, IOException {
        String content = rawData.get();
        return mapper.fromJson(content, valueType);
    }
    
}
