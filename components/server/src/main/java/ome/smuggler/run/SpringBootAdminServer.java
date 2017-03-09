package ome.smuggler.run;

import static ome.smuggler.config.items.SpringBootAdminConfigProps.adminServerUrl;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import ome.smuggler.config.Profiles;
import de.codecentric.boot.admin.config.EnableAdminServer;


/**
 * Runs the <a href="https://github.com/codecentric/spring-boot-admin">Spring 
 * Boot Admin</a> server on port {@code p + 1}, where {@code p} is the
 * {@link ImportServer}'s port.
 */
@EnableAutoConfiguration
@EnableAdminServer
public class SpringBootAdminServer implements RunnableApp {

    @Override
    public void run(List<String> appArgs) {
        SpringApplication app = new SpringApplication(
                                        SpringBootAdminServer.class);
        app.setAdditionalProfiles(Profiles.Prod);
        
        app.run();  // could pass in appArgs if needed
    }
    
    @Bean
    public UndertowEmbeddedServletContainerFactory 
                embeddedServletContainerFactory(Environment env) {
        int port = adminServerUrl()
                  .makeConfigReader(env::getProperty)
                  .defaultReadConfig()
                  .findFirst()
                  .get()
                  .getPort();
        return new UndertowEmbeddedServletContainerFactory(port);
    }
    
}
