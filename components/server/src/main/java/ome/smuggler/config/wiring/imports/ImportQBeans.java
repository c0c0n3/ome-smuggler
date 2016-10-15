package ome.smuggler.config.wiring.imports;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;
import org.hornetq.api.core.HornetQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulableFactory;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportQBeans {

    private SinkWriter<QueuedImport, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    private SourceReader<InputStream, QueuedImport> deserializer() {
        return new JsonInputStreamReader<>(QueuedImport.class);
    }

    @Bean
    public QChannelFactory<QueuedImport> importChannelFactory(
            ServerConnector connector, ImportQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedImport> importSourceChannel(
            QChannelFactory<QueuedImport> factory) throws HornetQException {
        return factory.buildSource(serializer());
    }
    
    @Bean
    public DequeueTask<QueuedImport> dequeueImportTask(
            QChannelFactory<QueuedImport> factory,
            ImportConfigSource importConfig,
            ImportProcessor processor,
            FailedImportHandler failureHandler) throws HornetQException {
        Reschedulable<QueuedImport> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        importConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }
    
}
