package ome.smuggler.q;

import java.io.StringReader;
import java.io.StringWriter;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.providers.json.JsonSinkWriter;
import ome.smuggler.providers.json.JsonSourceReader;

import org.hornetq.api.core.client.ClientMessage;

/**
 * Converters to read/write serialized data to/from a message body.
 */
public class MessageBody {

    public static <T> String writeBody(ClientMessage msg, T body) {
        StringWriter sink = new StringWriter();
        SinkWriter<T> writer = new JsonSinkWriter<>(sink);
        
        writer.uncheckedWrite(body);
        String serializedData = sink.toString();
        msg.getBodyBuffer().writeUTF(serializedData);
        
        return serializedData;
    }
    
    public static <T> T readBody(ClientMessage msg, Class<T> valueType) {
        String serializedData = msg.getBodyBuffer().readUTF();
        StringReader source = new StringReader(serializedData);
        SourceReader<T> reader = new JsonSourceReader<>(valueType, source);
        
        return reader.uncheckedRead();
    }
    
}
