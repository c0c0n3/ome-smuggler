package ome.smuggler.core.types;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.items.MailConfig;

/**
 * Provides read-only, type-safe access to mail configuration.
 * @see MailConfig
 */
public interface MailConfigSource {

    /**
     * @return the "from" address of each and every mail we send.
     */
    Email fromAddress();
    
    /**
     * @return the host and port of the mail server.
     */
    URI mailServer();
    
    /**
     * @return intervals at which to retry failed mail relays.
     */
    List<Duration> retryIntervals();
    
    /**
     * @return path to the directory where to keep email messages that could not
     * be relayed to the mail server.
     */
    Path deadMailDir();
    
}
