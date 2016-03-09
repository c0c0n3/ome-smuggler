package ome.smuggler.config.wiring.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.mail.impl.MailEnv;
import ome.smuggler.core.service.mail.impl.MailFailureHandler;
import ome.smuggler.core.service.mail.impl.MailTrigger;
import ome.smuggler.core.service.mail.impl.Mailer;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.PlainTextMail;

/**
 * Spring bean wiring configuration for the mail service interfaces.
 */
@Configuration
public class MailServiceBeans {

    @Bean
    public MailEnv mailEnv(MailConfigSource config, 
            ChannelSource<PlainTextMail> mailSourceChannel) {
        MailEnv env = new MailEnv(config, mailSourceChannel);
        
        env.ensureDirectories();
        return env;
    }
    
    @Bean 
    public MailRequestor mailRequestor(MailEnv env) {
        return new MailTrigger(env);
    }
    
    @Bean
    public MailProcessor mailProcessor() {
        return new Mailer();
    }
    
    @Bean
    public FailedMailHandler failedMailHandler() {
        return new MailFailureHandler();
    }
    
}
