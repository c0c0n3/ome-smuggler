package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies how to configure import logs.
 * The available settings:
 * <ul>
 *  <li>{@link #setImportLogDir(String) Log directory path}.
 *  Path to the directory where all import logs will be kept. Directories in the
 *  path will be created if needed. 
 *  </li>
 *  <li>{@link #setLogRetentionMinutes(Long) Log file retention period}.
 *  How long to keep an import run's log around after the import has completed.
 *  Past the specified amount of minutes, the file will be deleted.
 *  </li>
 *  <li>{@link #setRetryIntervals(Long[]) Retry intervals}.
 *  How many times and at which intervals to retry a failed import.
 *  If an import fails, it will be retried after the number of minutes given in
 *  the first slot of this array; if the retry fails, it will be attempted again
 *  after the number of minutes given in the second slot of this array; and so
 *  on until the import succeeds or the last slot is used. So a failed import
 *  will be retried at most a number of times equal to the length of this array;
 *  if the array is {@code null} or empty, no retries will ever be attempted.
 *  </li>
 *  <li>{@link #setFailedImportLogDir(String) Failed log directory path}.
 *  Path to the directory where to copy the log files of failed imports. 
 *  Directories in the path will be created if needed. 
 *  Note that the original file will still be available in the {@link 
 *  #getImportLogDir() log directory} but will be removed past the {@link 
 *  #getLogRetentionMinutes() retention period}. However, its copy in the 
 *  failed log directory will be kept indefinitely, the system administrator 
 *  will have to explicitly delete it after resolving the issue that caused 
 *  the failure.
 *  </li>
 *  <li>{@link #setKeepAliveInterval(Long) Session keep alive interval}.
 *  Interval, in minutes, at which to refresh the OMERO session of a queued 
 *  import. Even though the client that requested the import established already 
 *  an import session with OMERO, the import request may sit in the queue for
 *  a period of time longer than the OMERO session timeout; hence a session
 *  a keep-alive is needed to avoid the session expiring before the import
 *  request is fetched from the queue and run. 
 *  This interval should be a few minutes less than the session timeout
 *  of the OMERO server and defaults to 5 minutes if not specified. (This is
 *  because the OMERO default session timeout is 10 minutes.)
 *  </li>
 * </ul>
 */
public class ImportConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private String importLogDir;
    private Long logRetentionMinutes;
    private Long[] retryIntervals;
    private String failedImportLogDir;
    private Long keepAliveInterval;
    
    public String getImportLogDir() {
        return importLogDir;
    }
    
    public void setImportLogDir(String importLogDir) {
        this.importLogDir = importLogDir;
    }

    public Long getLogRetentionMinutes() {
        return logRetentionMinutes;
    }

    public void setLogRetentionMinutes(Long retentionMinutes) {
        this.logRetentionMinutes = retentionMinutes;
    }
    
    public Long[] getRetryIntervals() {
        return retryIntervals;
    }

    public void setRetryIntervals(Long[] retryIntervals) {
        this.retryIntervals = retryIntervals;
    }
    
    public String getFailedImportLogDir() {
        return failedImportLogDir;
    }

    public void setFailedImportLogDir(String failedImportLogDir) {
        this.failedImportLogDir = failedImportLogDir;
    }
    
    public Long getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
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
        if (other instanceof ImportConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        String xs = Arrays.toString(retryIntervals);
        return String.format("%s | %s | %s | %s | %s",
                importLogDir, failedImportLogDir, logRetentionMinutes, 
                keepAliveInterval, xs);
    }
    
}
