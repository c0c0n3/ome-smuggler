package ome.smuggler.q;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.junit.Test;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSink;

public class DequeueTaskTest implements ChannelSink<String> {

    private String receivedMsg;
    
    private DequeueTask<String> newTask() throws HornetQException {
        ImportQConfig q = new ImportQConfig();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        ClientConsumer receiver = mock(ClientConsumer.class);
        when(sesh.createConsumer(q.getName(), false)).thenReturn(receiver);
        
        QueueConnector connector = new QueueConnector(q, sesh);
        DequeueTask<String> task = 
                new DequeueTask<>(connector, this, String.class);
        verify(receiver).setMessageHandler(task);
        
        return task;
    }
    
    @Override
    public void consume(String msg) {
        receivedMsg = msg;   
    }

    @Test
    public void receiveMessage() throws HornetQException {
        DequeueTask<String> task = newTask();
        String msg = "msg";
        
        ClientMessage qMsg = mock(ClientMessage.class);
        HornetQBuffer buf = mock(HornetQBuffer.class);
        when(qMsg.getBodyBuffer()).thenReturn(buf);
        when(buf.readUTF()).thenReturn(msg);
        
        task.onMessage(qMsg);
        
        assertThat(receivedMsg, is(msg));
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfFirstArgNull() throws HornetQException {
        new DequeueTask<>(null, s -> {}, String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfSecondArgNull() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), null, String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfThirdArgNull() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), s -> {}, null);
    }
        
}
