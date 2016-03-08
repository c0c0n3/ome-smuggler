package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies how to configure the mail service.
 * The available settings:
 * <ul>
 *  <li>{@link #setMailServerHost(String) Mail server host}. The host name of 
 *  the machine where the mail server runs. Mail will be sent to this server.  
 *  </li>
 *  <li>{@link #setMailServerPort(int) Mail server port}. The port of the mail
 *  server given above.
 *  </li>
 *  <li>{@link MailConfig#setFromAddress(String) From address}. The "from" 
 *  address of each and every mail we send.
 *  </li>
 *  <li>{@link #setRetryIntervals(Long[]) Retry intervals}.
 *  How many times and at which intervals to retry a failed mail relay.
 *  If a relay fails, it will be retried after the number of minutes given in
 *  the first slot of this array; if the retry fails, it will be attempted again
 *  after the number of minutes given in the second slot of this array; and so
 *  on until the relay succeeds or the last slot is used. So a failed relay
 *  will be retried at most a number of times equal to the length of this array;
 *  if the array is {@code null} or empty, no retries will ever be attempted.
 *  </li>
 *  <li>{@link #setDeadMailDir(String) Dead mail directory}.
 *  Path to the directory where to store email messages that could not be 
 *  relayed to the mail server, that is, messages whose delivery failed past
 *  the configured number of retries as explained above. 
 *  A message in the dead mail directory will be kept indefinitely, the system 
 *  administrator will have to explicitly delete it after re-sending it to the
 *  intended recipient, for example by inputting the file into a program such
 *  as {@code sendmail}.
 *  Note that directories in the given path will be created if needed.
 *  </li>
 * </ul>
 */
public class MailConfig {
    // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
    // be (de-)serialized painlessly by SnakeYaml.
    
    private String fromAddress;
    private String mailServerHost;
    private int mailServerPort;
    private Long[] retryIntervals;
    private String deadMailDir;
    
    public String getMailServerHost() {
        return mailServerHost;
    }
    
    public void setMailServerHost(String host) {
        this.mailServerHost = host;
    }
    
    public int getMailServerPort() {
        return mailServerPort;
    }
    
    public void setMailServerPort(int port) {
        this.mailServerPort = port;
    }
    
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public Long[] getRetryIntervals() {
        return retryIntervals;
    }

    public void setRetryIntervals(Long[] retryIntervals) {
        this.retryIntervals = retryIntervals;
    }

    public String getDeadMailDir() {
        return deadMailDir;
    }

    public void setDeadMailDir(String failedRelayDir) {
        this.deadMailDir = failedRelayDir;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MailConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        String xs = Arrays.toString(retryIntervals);
        return String.format("%s | %s | %s | %s | %s", mailServerHost, 
                mailServerPort, fromAddress, deadMailDir, xs);
    }
    
}