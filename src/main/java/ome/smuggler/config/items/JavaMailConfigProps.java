package ome.smuggler.config.items;

import static util.config.StringConfigReaderFactory.intParser;
import static util.config.props.JPropAccessorFactory.makeBool;
import static util.config.props.JPropAccessorFactory.makeEnum;
import static util.config.props.JPropAccessorFactory.makeString;
import static util.config.props.JPropKey.key;

import java.time.Duration;

import util.config.props.JPropAccessor;
import util.config.props.JPropKey;

/**
 * Accessors for Java Mail SMTP properties.
 * @see <a href="https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html">
 * Java Mail SMTP summary</a>.
 */
public class JavaMailConfigProps {
    
    private static 
    JPropAccessor<Duration> makeDurationOfMillis(JPropKey k) {
        return new JPropAccessor<>(k, 
                intParser().andThen(Duration::ofMillis), 
                d -> String.valueOf(d.toMillis()));
    }
    // NB using intParser as all millis values in the java SMTP mail props page
    // are of type int.
    
    public static
    JPropAccessor<Boolean> debug() {
        return makeBool(key("mail.debug"));
    }
    
    public static
    JPropAccessor<MailProtocol> transportProtocol() {
        return makeEnum(MailProtocol.class, key("mail.transport.protocol"));
    }
    
    public static
    JPropAccessor<Duration> smtpConnectionTimeout() {
        return makeDurationOfMillis(key("mail.smtp.connectiontimeout"));
    }
    
    public static
    JPropAccessor<Duration> smtpReadTimeout() {
        return makeDurationOfMillis(key("mail.smtp.timeout"));
    }
    
    public static
    JPropAccessor<Duration> smtpWriteTimeout() {
        return makeDurationOfMillis(key("mail.smtp.writetimeout"));
    }
    
    public static
    JPropAccessor<Boolean> smtpAuthenticate() {
        return makeBool(key("mail.smtp.auth"));
    }
    
    public static
    JPropAccessor<Boolean> smtpEnableSsl() {
        return makeBool(key("mail.smtp.ssl.enable"));
    }
    
    public static
    JPropAccessor<Boolean> smtpCheckServerIdentity() {
        return makeBool(key("mail.smtp.ssl.checkserveridentity"));
    }
    
    public static
    JPropAccessor<String> smtpTrustedServers() {
        return makeString(key("mail.smtp.ssl.trust"));
    }
    
    public static
    JPropAccessor<Boolean> smtpEnableStartTls() {
        return makeBool(key("mail.smtp.starttls.enable"));
    }
    
    public static
    JPropAccessor<Boolean> smtpRequireStartTls() {
        return makeBool(key("mail.smtp.starttls.required"));
    }
    
}
