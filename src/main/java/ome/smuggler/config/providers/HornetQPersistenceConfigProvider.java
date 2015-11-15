package ome.smuggler.config.providers;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.DefaultHornetQPersistenceConfig;


/**
 * The HornetQ operational parameters.
 * This configuration is hard-coded as it is only used internally by the
 * import server.
 */
@Component
public class HornetQPersistenceConfigProvider 
    extends DefaultHornetQPersistenceConfig {

}
