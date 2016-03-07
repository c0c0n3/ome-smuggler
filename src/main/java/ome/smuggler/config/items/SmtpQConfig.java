package ome.smuggler.config.items;

import org.hornetq.core.config.CoreQueueConfiguration;

/**
 * Holds the SMTP queue configuration.
 */
public class SmtpQConfig extends CoreQueueConfiguration {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.
     * CoreQueueConfiguration is only missing a no args ctor which we add below.  
     */ 

    private static final long serialVersionUID = 1L;

    public SmtpQConfig() { 
        super("", "", "", false);
    }
    
    public SmtpQConfig(String address, String name, String filterString,
            boolean durable) {
        super(address, name, filterString, durable);
    }

}
