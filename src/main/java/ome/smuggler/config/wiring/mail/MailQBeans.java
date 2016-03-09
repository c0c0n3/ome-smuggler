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
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.q.DequeueTask;
import ome.smuggler.q.QChannelFactory;
import ome.smuggler.q.ServerConnector;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class MailQBeans {

    @Bean
    public QChannelFactory<PlainTextMail> mailChannelFactory(
            ServerConnector connector, MailQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<PlainTextMail> mailSourceChannel(
            QChannelFactory<PlainTextMail> factory) throws HornetQException {
        return factory.buildSource();
    }
    
    @Bean
    public DequeueTask<PlainTextMail> dequeueMailTask(
            QChannelFactory<PlainTextMail> factory,
            MailConfigSource mailConfig,
            MailProcessor processor,
            FailedMailHandler failureHandler) throws HornetQException {
        Reschedulable<PlainTextMail> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        mailConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer, PlainTextMail.class);
    }

}
