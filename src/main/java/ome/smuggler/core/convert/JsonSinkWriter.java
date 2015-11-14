package ome.smuggler.core.convert;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

/**
 * Serializes an object into JSON and writes the serialized data to a sink.
 */
public class JsonSinkWriter<T> implements SinkWriter<T> {

    private final Gson mapper;
    private final Consumer<String> rawMsgBody;
    
    /**
     * Creates a new instance to serialize data into the specified consumer.
     * @param rawData accepts the serialized data and writes it to the sink.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonSinkWriter(Consumer<String> rawData) {
        requireNonNull(rawData, "rawData");
        
        this.mapper = new Gson();
        this.rawMsgBody = rawData;
    }
    
    @Override
    public void write(T body) throws JsonProcessingException {
        String content = mapper.toJson(body);
        rawMsgBody.accept(content);
    }

}
