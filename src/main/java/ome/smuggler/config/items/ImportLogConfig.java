package ome.smuggler.config.items;

import java.util.Objects;

/**
 * Specifies how to configure import logs.
 * The available settings:
 * <ul>
 *  <li>{@link #setImportLogDir(String) Log directory path}.
 *  Path to the directory where all import logs will be kept. Directories in the
 *  path will be created if needed. 
 *  </li>
 *  <li>{@link #setRetentionMinutes(Long) file retention period}.
 *  How long to keep an import run's log around after the import has completed.
 *  Past the specified amount of minutes, the file will be deleted.
 *  </li>
 * </ul>
 */
public class ImportLogConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private String importLogDir;
    private Long retentionMinutes;
    
    public String getImportLogDir() {
        return importLogDir;
    }
    
    public void setImportLogDir(String importLogDir) {
        this.importLogDir = importLogDir;
    }

    public Long getRetentionMinutes() {
        return retentionMinutes;
    }

    public void setRetentionMinutes(Long retentionMinutes) {
        this.retentionMinutes = retentionMinutes;
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
        if (other instanceof ImportLogConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s", importLogDir, retentionMinutes);
    }
    
}
