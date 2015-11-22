package ome.smuggler.q;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.client.ClientMessage;
import org.junit.Test;

public class MessageBodyTest {

    @Test
    public void writeThenRead() {
        ClientMessage msg = mock(ClientMessage.class);
        HornetQBuffer buf = mock(HornetQBuffer.class);
        when(msg.getBodyBuffer()).thenReturn(buf);
        
        String initialValue = "body";
        String serialized = MessageBody.writeBody(msg, initialValue);
        verify(buf).writeUTF(serialized);
        
        when(buf.readUTF()).thenReturn(serialized);
        String deserialized = MessageBody.readBody(msg, String.class);
        
        assertThat(deserialized, is(initialValue));
    }
    
}
