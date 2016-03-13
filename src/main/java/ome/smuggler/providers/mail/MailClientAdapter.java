package ome.smuggler.providers.mail;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpAuthenticate;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpConnectionTimeout;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpReadTimeout;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpWriteTimeout;
import static ome.smuggler.config.items.JavaMailConfigProps.transportProtocol;

import java.time.Duration;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import ome.smuggler.core.service.mail.MailClient;
import ome.smuggler.core.types.MailConfigSource;
import util.config.props.JProps;

public class MailClientAdapter implements MailClient {
    
    public static final Duration OpTimeout = Duration.ofMinutes(5); 
    
    private static JavaMailSender build(MailConfigSource config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(config.mailServer().getHost());
        mailSender.setPort(config.mailServer().getPort());
        config.username().ifPresent(u -> mailSender.setUsername(u));
        config.password().ifPresent(p -> mailSender.setPassword(p));
        
        JProps mailProps = new JProps(mailSender.getJavaMailProperties());
        config.username().ifPresent(
                u -> mailProps.set(smtpAuthenticate().with(true)));
        mailProps.set(transportProtocol().with(config.protocol()));
        
        mailProps.set(smtpConnectionTimeout().with(OpTimeout));
        mailProps.set(smtpReadTimeout().with(OpTimeout));
        mailProps.set(smtpWriteTimeout().with(OpTimeout));
        
        return mailSender;
    }
    
    private final JavaMailSender service;
    
    public MailClientAdapter(MailConfigSource config) {
        requireNonNull(config, "config");
        service = build(config);
    }
    
    @Override
    public MimeMessage createMimeMessage() {
        return service.createMimeMessage();
    }

    @Override
    public void send(MimeMessage data) {
        service.send(data);
    }

}
