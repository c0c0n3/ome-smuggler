package ome.smuggler.config.wiring.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.mail.impl.MailEnv;
import ome.smuggler.core.service.mail.impl.MailFailureHandler;
import ome.smuggler.core.service.mail.impl.MailTrigger;
import ome.smuggler.core.service.mail.impl.Mailer;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.QueuedMail;

/**
 * Spring bean wiring configuration for the mail service interfaces.
 */
@Configuration
public class MailServiceBeans {

    @Bean
    public JavaMailSender javaMailSender(MailConfigSource config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.mailServer().getHost());
        mailSender.setPort(config.mailServer().getPort());
        config.username().ifPresent(u -> mailSender.setUsername(u));
        config.password().ifPresent(p -> mailSender.setPassword(p));
        
        return mailSender;
    }
    
    @Bean
    public MailEnv mailEnv(MailConfigSource config, 
            ChannelSource<QueuedMail> mailSourceChannel,
            JavaMailSender mailClient) {
        MailEnv env = new MailEnv(config, mailSourceChannel, mailClient);
        
        env.ensureDirectories();
        return env;
    }
    
    @Bean 
    public MailRequestor mailRequestor(MailEnv env) {
        return new MailTrigger(env);
    }
    
    @Bean
    public MailProcessor mailProcessor(MailEnv env) {
        return new Mailer(env);
    }
    
    @Bean
    public FailedMailHandler failedMailHandler(MailEnv env) {
        return new MailFailureHandler(env);
    }
    
}
