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
        ClientSession session = csf.createSession();
        session.start();
        return session;
    }
    
}
