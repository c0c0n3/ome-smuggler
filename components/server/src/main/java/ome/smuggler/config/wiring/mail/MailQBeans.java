package ome.smuggler.config.wiring.mail;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulableFactory;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.QueuedMail;
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
public class MailQBeans {

    private SinkWriter<QueuedMail, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    private SourceReader<InputStream, QueuedMail> deserializer() {
        return new JsonInputStreamReader<>(QueuedMail.class);
    }

    @Bean
    public QChannelFactory<QueuedMail> mailChannelFactory(
            ServerConnector connector, MailQConfig qConfig) {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedMail> mailSourceChannel(
            QChannelFactory<QueuedMail> factory) throws ActiveMQException {
        return factory.buildSource(serializer());
    }
    
    @Bean
    public DequeueTask<QueuedMail> dequeueMailTask(
            QChannelFactory<QueuedMail> factory,
            MailConfigSource mailConfig,
            MailProcessor processor,
            FailedMailHandler failureHandler) throws ActiveMQException {
        Reschedulable<QueuedMail> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        mailConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }

}
