package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

/**
 * Establishes a connection and client session with the Artemis server.
 */
public class ServerConnector {

    private static ClientSession startSession(ClientSessionFactory csf) 
            throws ActiveMQException {
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
     *
     * Now just looking at the Artemis API, it seems de-queueing of messages
     * should work exactly the same as it used to in HornetQ. In other words,
     * what noted above applies to Artemis too.
     * TODO confirm this!
     */
    
    private final ClientSessionFactory factory;
    private final ClientSession session;
    
    /**
     * Connects to the Artemis server and starts a client session.
     * @param locator locates the Artemis server.
     * @throws Exception if the connection could not be established or the
     * session could not be started.
     */
    public ServerConnector(ServerLocator locator) throws Exception {
        requireNonNull(locator, "locator");
        
        this.factory = locator.createSessionFactory();
        this.session = startSession(factory);
    }

    /**
     * @return the current Artemis session.
     */
    public ClientSession getSession() {
        return session;
    }
    
}
