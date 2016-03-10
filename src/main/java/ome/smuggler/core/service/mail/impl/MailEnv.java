package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import java.nio.file.Path;

import org.springframework.mail.javamail.JavaMailSender;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.types.FailedMailPath;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.QueuedMail;

/**
 * Provides access to mail configuration and queue.
 */
public class MailEnv {

    private final MailConfigSource config;
    private final ChannelSource<QueuedMail> queue;
    private final JavaMailSender mailClient;
    
    public MailEnv(MailConfigSource config, ChannelSource<QueuedMail> queue, 
                   JavaMailSender mailClient) {
        requireNonNull(config, "config");
        requireNonNull(queue, "queue");
        requireNonNull(mailClient, "mailClient");
        
        this.config = config;
        this.queue = queue;
        this.mailClient = mailClient;
    }
    
    public MailConfigSource config() {
        return config;
    }
    
    public ChannelSource<QueuedMail> queue() {
        return queue;
    }
    
    public JavaMailSender mailClient() {
        return mailClient;
    }
    
    public Path failedMailPathFor(MailId taskId) {
        return new FailedMailPath(config.deadMailDir(), taskId).get();
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().deadMailDir());
    }
    
}
