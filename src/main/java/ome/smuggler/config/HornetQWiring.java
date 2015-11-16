package ome.smuggler.config;

import javax.jms.ConnectionFactory;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class HornetQWiring {
    
    @Bean
    public ServerLocator hornetQServerLocator(ConnectionFactory cf) {
        return ((HornetQConnectionFactory)cf).getServerLocator();
    }
    
    @Bean
    public ClientSessionFactory hornetQClientSessionFactory(ServerLocator sl) 
            throws Exception {
        return sl.createSessionFactory();
    }
    
    @Bean
    public ClientSession hornetQClientSession(ClientSessionFactory csf) 
            throws HornetQException {
        ClientSession session = csf.createSession(true, true, 0);  // (*)
        session.start();
        return session;
    }
    /* (*) Removal of messages from the queue.
     * When using the HornetQ core API, consumed messages have to be explicitly 
     * acknowledged for them to be removed from the queue. However, the core API
     * will batch ACK's and send them in one go when the configured batch size
     * is reached. This may cause consumed and acknowledged messages to linger
     * in the queue; by setting the ACK batch size to 0, we ensure messages will
     * be removed as soon as they are acknowledged. 
     * See
     * - http://stackoverflow.com/questions/6452505/hornetq-messages-still-remaining-in-queue-after-consuming-using-core-api
     */
}
