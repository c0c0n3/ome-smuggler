package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static ome.smuggler.core.msg.RepeatAction.Stop;
import static ome.smuggler.core.service.Loggers.logTransientError;
import static ome.smuggler.core.service.mail.impl.MailLoggers.logMailSent;

import javax.mail.internet.MimeMessage;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.types.QueuedMail;

public class Mailer implements MailProcessor {

    private final MailEnv env;
    
    public Mailer(MailEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    @Override
    public RepeatAction consume(QueuedMail data) {
        MessageBuilder builder = new MessageBuilder(env, data.getRequest());
        MimeMessage message = builder.buildMimeMessage();
        try {
            env.mailClient().send(message);
            
            logMailSent(data);
            return Stop;
        } catch (Exception e) {
            logTransientError(this, e);
            return Repeat;
        }
    }
    
}
