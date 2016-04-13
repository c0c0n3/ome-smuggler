package ome.smuggler.config.wiring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.Profiles;
import ome.smuggler.config.data.DefaultHornetQPersistenceConfig;
import ome.smuggler.config.items.HornetQPersistenceConfig;

/**
 * Spring bean wiring of configuration items for the {@link Profiles#Dev Dev}
 * profile.
 */
@Configuration
@Profile(Profiles.Dev)
public class HornetQDevConfigBeans {

    @Bean
    public HornetQPersistenceConfig hornetQPersistenceConfig() {
        return DefaultHornetQPersistenceConfig.build(new BaseDataDir().get());
    }
    
}
