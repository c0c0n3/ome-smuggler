package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.io.FileOps.ensureDirectory;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.PlainTextMail;

/**
 * Provides access to mail configuration and queue.
 */
public class MailEnv {

    private final MailConfigSource config;
    private final ChannelSource<PlainTextMail> queue;
    
    public MailEnv(MailConfigSource config, ChannelSource<PlainTextMail> queue) {
        requireNonNull(config, "config");
        requireNonNull(queue, "queue");
        
        this.config = config;
        this.queue = queue;
    }
    
    public MailConfigSource config() {
        return config;
    }
    
    public ChannelSource<PlainTextMail> queue() {
        return queue;
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().deadMailDir());
    }
    
}
