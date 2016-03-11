package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMailMessage;

import ome.smuggler.core.types.PlainTextMail;


public class MessageBuilder {

    private final MailEnv env;
    private final PlainTextMail data;
    
    public MessageBuilder(MailEnv env, PlainTextMail data) {
        requireNonNull(env, "env");
        requireNonNull(data, "data");
        
        this.env = env;
        this.data = data;
    }
    
    public MimeMessage buildMimeMessage() {
        MimeMailMessage builder = new MimeMailMessage(
                                    env.mailClient().createMimeMessage());
        builder.setFrom(env.config().fromAddress().get());
        builder.setTo(data.getRecipient().get());
        builder.setSubject(data.getSubject());
        builder.setText(data.getContent());
        
        return builder.getMimeMessage();
    }
    
}