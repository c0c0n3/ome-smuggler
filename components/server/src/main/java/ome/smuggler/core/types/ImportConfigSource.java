package ome.smuggler.core.types;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.items.ImportConfig;

/**
 * Provides read-only, type-safe access to the import configuration.
 * @see ImportConfig
 */
public interface ImportConfigSource {

    /**
     * @return path to the directory where to keep import logs.
     */
    Path importLogDir();
    
    /**
     * @return how long to keep import logs before deleting them.
     */
    Duration logRetentionPeriod();
    
    /**
     * @return intervals at which to retry failed imports.
     */
    List<Duration> retryIntervals();
    
    /**
     * @return path to the directory where to keep logs of failed imports.
     */
    Path failedImportLogDir();
    
    /**
     * @return at which interval to refresh the OMERO session of a queued 
     * import.
     */
    Duration keepAliveInterval();
    
}
