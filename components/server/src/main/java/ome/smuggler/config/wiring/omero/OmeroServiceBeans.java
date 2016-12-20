package ome.smuggler.config.wiring.omero;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.service.omero.impl.Importer;
import ome.smuggler.core.service.omero.impl.OmeroEnv;
import ome.smuggler.core.service.omero.impl.SessionManager;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.OmeCliConfigSource;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import ome.smuggler.providers.log.LogAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration for the OMERO service interfaces.
 */
@Configuration
public class OmeroServiceBeans {

    @Bean
    public OmeroEnv omeroEnv(
            OmeCliConfigSource config,
            ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel) {
        return new OmeroEnv(config, sessionSourceChannel, new LogAdapter());
    }

    @Bean
    public ImportService importService(OmeroEnv env, ImportConfigSource cfg) {
        return new Importer(env, cfg);
    }

    @Bean
    public SessionService sessionService(OmeroEnv env) {
        return new SessionManager(env);
    }

}
