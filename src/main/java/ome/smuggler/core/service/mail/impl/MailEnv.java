package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.QueuedMail;

/**
 * Provides access to mail configuration and queue.
 */
public class MailEnv {

    private final MailConfigSource config;
    private final ChannelSource<QueuedMail> queue;
    
    public MailEnv(MailConfigSource config, ChannelSource<QueuedMail> queue) {
        requireNonNull(config, "config");
        requireNonNull(queue, "queue");
        
        this.config = config;
        this.queue = queue;
    }
    
    public MailConfigSource config() {
        return config;
    }
    
    public ChannelSource<QueuedMail> queue() {
        return queue;
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().deadMailDir());
    }
    
}
