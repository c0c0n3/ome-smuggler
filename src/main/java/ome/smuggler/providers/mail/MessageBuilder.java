package ome.smuggler.providers.mail;

import static java.util.Objects.requireNonNull;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;

import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.PlainTextMail;


public class MessageBuilder {

    private final MailConfigSource config;
    private final JavaMailSender mailClient;
    
    public MessageBuilder(MailConfigSource config, JavaMailSender mailClient) {
        requireNonNull(config, "config");
        requireNonNull(mailClient, "mailClient");
        
        this.config = config;
        this.mailClient = mailClient;
    }
    
    public MimeMessage buildMimeMessage(PlainTextMail data) {
        requireNonNull(data, "data");
        
        MimeMailMessage builder = new MimeMailMessage(
                                        mailClient.createMimeMessage());
        builder.setFrom(config.fromAddress().get());
        builder.setTo(data.getRecipient().get());
        builder.setSubject(data.getSubject());
        builder.setText(data.getContent());
        
        return builder.getMimeMessage();
    }
    
}
