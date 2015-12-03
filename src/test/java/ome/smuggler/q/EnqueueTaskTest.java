package ome.smuggler.q;

import static org.mockito.Mockito.*;

import org.hornetq.api.core.HornetQException;
import org.junit.Test;

import ome.smuggler.core.msg.ChannelSource;


public class EnqueueTaskTest extends BaseSendTest {
    
    private ChannelSource<String> newTask() throws HornetQException {
        initMocks();
        return new EnqueueTask<String>(connector).asDataSource();
    }
    
    @Test
    public void sendMessage() throws HornetQException {
        newTask().uncheckedSend("msg");
        
        verify(msgBody).writeUTF(any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfCtorArgNull() throws HornetQException {
        new EnqueueTask<>(null);
    }
    
}
