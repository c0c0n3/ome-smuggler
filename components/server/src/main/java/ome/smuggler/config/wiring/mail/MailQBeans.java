package ome.smuggler.config.wiring.mail;

import org.hornetq.api.core.HornetQException;
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

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class MailQBeans {

    @Bean
    public QChannelFactory<QueuedMail> mailChannelFactory(
            ServerConnector connector, MailQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedMail> mailSourceChannel(
            QChannelFactory<QueuedMail> factory) throws HornetQException {
        return factory.buildSource();
    }
    
    @Bean
    public DequeueTask<QueuedMail> dequeueMailTask(
            QChannelFactory<QueuedMail> factory,
            MailConfigSource mailConfig,
            MailProcessor processor,
            FailedMailHandler failureHandler) throws HornetQException {
        Reschedulable<QueuedMail> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        mailConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer, QueuedMail.class);
    }

}
