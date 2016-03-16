package integration.web;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.BaseStringId;

@Configuration
public class IntegrationWiring {
     
    @Bean
    public TaskFileStore<BaseStringId> taskFileStore() throws IOException {
        return new TempDirTaskStore(2);
    }
    
    @Bean
    public TaskFileStoreController taskFileStoreController() {
        return new TaskFileStoreController();
    }
    
}
