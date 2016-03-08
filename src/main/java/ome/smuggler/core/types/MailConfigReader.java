package ome.smuggler.core.types;

import static java.util.function.Function.identity;
import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.convert.RawConfigValues.toDurationList;
import static ome.smuggler.core.convert.RawConfigValues.toPath;
import static ome.smuggler.core.convert.RawConfigValues.toURI;
import static ome.smuggler.core.types.ValueParserFactory.email;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.items.MailConfig;

/**
 * Implements {@link MailConfigSource} by extracting and validating values 
 * from an underlying {@link MailConfig}.
 */
public class MailConfigReader implements MailConfigSource {

    private final Email fromAddress;
    private final URI mailServer;
    private final List<Duration> retryIntervals;
    private final Path deadMailDir;
    
    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration 
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public MailConfigReader(MailConfig config) {
        requireNonNull(config, "config");
        
        fromAddress = email(config.getFromAddress())
                     .either(errorMsg -> {
                                 throw new IllegalArgumentException(errorMsg);
                             }, identity());
        mailServer = toURI("smtp", config.getMailServerHost(), 
                            config.getMailServerPort());
        retryIntervals = toDurationList(config.getRetryIntervals());
        deadMailDir = toPath(config.getDeadMailDir());
    }
    
    @Override
    public Email fromAddress() {
        return fromAddress;
    }

    @Override
    public URI mailServer() {
        return mailServer;
    }

    @Override
    public List<Duration> retryIntervals() {
        return retryIntervals;
    }

    @Override
    public Path deadMailDir() {
        return deadMailDir;
    }

}
