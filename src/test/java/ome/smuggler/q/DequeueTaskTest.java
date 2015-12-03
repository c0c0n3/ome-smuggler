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
import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.MessageSink;

public class DequeueTaskTest implements MessageSink<ClientMessage, String> {

    private ClientMessage receivedMsg;
    private String receivedData;
    
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
    public void consume(ChannelMessage<ClientMessage, String> msg) {
        receivedMsg = msg.metadata().get();
        receivedData = msg.data();
        
    }

    @Test
    public void receiveMessage() throws HornetQException {
        DequeueTask<String> task = newTask();
        String msgData = "msg";
        
        ClientMessage qMsg = mock(ClientMessage.class);
        HornetQBuffer buf = mock(HornetQBuffer.class);
        when(qMsg.getBodyBuffer()).thenReturn(buf);
        when(buf.readUTF()).thenReturn(msgData);
        
        task.onMessage(qMsg);
        
        assertThat(receivedMsg, is(qMsg));
        assertThat(receivedData, is(msgData));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg1Null() throws HornetQException {
        new DequeueTask<>(null, (ChannelSink<String>) d -> {}, String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg2Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                          (ChannelSink<String>)null, String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg3Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                          (ChannelSink<String>) d -> {}, null);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg1Null() throws HornetQException {
        new DequeueTask<>(null, (MessageSink<ClientMessage, String>) msg -> {}, 
                          String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg2Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                (MessageSink<ClientMessage, String>)null, String.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg3Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                          (MessageSink<ClientMessage, String>) msg -> {}, 
                          (Class<String>) null);
    }
    
}
