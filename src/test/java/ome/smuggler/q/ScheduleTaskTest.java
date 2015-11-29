package ome.smuggler.q;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Message;
import org.junit.Test;

import ome.smuggler.core.msg.ConfigurableChannelSource;


public class ScheduleTaskTest extends BaseSendTest {
    
    private ConfigurableChannelSource<Duration, String> newTask() throws HornetQException {
        initMocks();
        when(msgToQueue.putLongProperty(anyString(), anyLong()))
        .thenReturn(msgToQueue);
        
        return new ScheduleTask<>(connector);
    }
    
    @Test
    public void sendMessage() throws HornetQException {
        newTask().uncheckedSend("msg");
        
        verify(msgBody).writeUTF(any());
        verify(producer).send(msgToQueue);
    }
    
    @Test
    public void scheduleMessage() throws HornetQException {
        Duration fromNow = Duration.ofMinutes(1);
        long expectedSchedule = System.currentTimeMillis() + fromNow.toMillis();
        
        newTask().uncheckedSend(fromNow, "msg");
        
        verify(msgToQueue).putLongProperty(
                eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), 
                longThat(greaterThanOrEqualTo(expectedSchedule)));
        verify(msgBody).writeUTF(any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfCtorArgNull() throws HornetQException {
        new ScheduleTask<>(null);
    }
    
}
