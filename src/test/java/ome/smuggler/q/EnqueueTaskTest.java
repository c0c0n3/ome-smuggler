package ome.smuggler.q;

import static org.mockito.Mockito.*;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.junit.Test;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSource;


public class EnqueueTaskTest {

    private ClientProducer producer;
    private ClientMessage msgToQueue;
    private HornetQBuffer msgBody;
    
    private ChannelSource<String> newTask() throws HornetQException {
        ImportQConfig q = new ImportQConfig();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        
        producer = mock(ClientProducer.class);
        when(sesh.createProducer(q.getAddress())).thenReturn(producer);
        
        msgToQueue = mock(ClientMessage.class);
        boolean durable = true;
        when(sesh.createMessage(durable)).thenReturn(msgToQueue);
        
        msgBody = mock(HornetQBuffer.class);
        when(msgToQueue.getBodyBuffer()).thenReturn(msgBody);
        
        return new EnqueueTask<>(q, sesh);
    }
    
    @Test
    public void sendMessage() throws HornetQException {
        newTask().uncheckedSend("msg");
        
        verify(msgBody).writeUTF(any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfFirstArgNull() throws HornetQException {
        new EnqueueTask<>(null, mock(ClientSession.class));
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfSecondArgNull() throws HornetQException {
        new EnqueueTask<>(new ImportQConfig(), null);
    }
    
}
