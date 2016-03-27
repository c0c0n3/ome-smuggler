package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.core.config.CoreQueueConfiguration;

import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSink;
import ome.smuggler.core.msg.MessageSource;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulingSink;
import ome.smuggler.core.msg.SchedulingSource;


public class QChannelFactory<T> {

    public static <T> QChannelFactory<T> with(ServerConnector connector, 
                                              CoreQueueConfiguration qConfig) {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    
    private final ServerConnector connector;
    private final CoreQueueConfiguration qConfig;
    
    public QChannelFactory(ServerConnector connector, 
                           CoreQueueConfiguration qConfig) {
        requireNonNull(connector, "connector");
        requireNonNull(qConfig, "qConfig");
        
        this.connector = connector;
        this.qConfig = qConfig;
    }
   
    private QueueConnector queue() {
        return new QueueConnector(qConfig, connector.getSession());
    }
    
    public ChannelSource<T> buildSource() throws HornetQException {
        MessageSource<Function<QueueConnector, ClientMessage>, T> task = // (*) 
                new EnqueueTask<>(queue());
        return task.asDataSource();  
    }
    /* (*) If the ascended Java masters had blessed us with a slightly less
     * delectable language, a one-liner might have worked: 
     * 
     *      return new EnqueueTask<>(q).asDataSource();
     *      
     * But instead we shall rejoice in type erasure and accept the delightful 
     * verbosity of Java with unstinting devotion. I shall repent of even 
     * mentioning this!
     * For the record, note that using a concrete type works:
     * 
     *      return new EnqueueTask<QueuedImport>(q).asDataSource();
     */
    
    public SchedulingSource<T> buildSchedulingSource() throws HornetQException {
        return new ScheduleTask<>(queue());  
    }
    
    public MessageSource<CountedSchedule, T> buildCountedScheduleSource()
            throws HornetQException {
        return new CountedScheduleTask<>(queue());
    }
    
    public DequeueTask<T> buildSink(ChannelSink<T> consumer, 
            Class<T> messageType) throws HornetQException {
        return new DequeueTask<>(queue(), consumer, messageType, true);
    }
    
    public DequeueTask<T> buildSink(ChannelSink<T> consumer, 
            Class<T> messageType, boolean redeliverOnCrash) 
                    throws HornetQException {
        return new DequeueTask<>(queue(), consumer, messageType, 
                                 redeliverOnCrash);
    }
    
    public DequeueTask<T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer, Class<T> messageType) 
                    throws HornetQException {
        return new DequeueTask<>(queue(), new CountedScheduleSink<>(consumer),
                                 messageType, true);
    }
    
    public DequeueTask<T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer, Class<T> messageType,
            boolean redeliverOnCrash) 
                    throws HornetQException {
        return new DequeueTask<>(queue(), new CountedScheduleSink<>(consumer),
                                 messageType, redeliverOnCrash);
    }
    
    public DequeueTask<T> buildReschedulableSink(Reschedulable<T> consumer, 
            Class<T> messageType) throws HornetQException {
        return buildReschedulableSink(consumer, messageType, true);
    }
    
    public DequeueTask<T> buildReschedulableSink(Reschedulable<T> consumer, 
            Class<T> messageType, boolean redeliverOnCrash) 
                    throws HornetQException {
        MessageSource<CountedSchedule, T> loopback = 
                buildCountedScheduleSource();
        ReschedulingSink<T> sink = new ReschedulingSink<>(consumer, loopback);
        return buildCountedScheduleSink(sink, messageType, redeliverOnCrash);
    }
    
}
