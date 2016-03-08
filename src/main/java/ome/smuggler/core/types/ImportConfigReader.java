package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.convert.RawConfigValues.toDuration;
import static ome.smuggler.core.convert.RawConfigValues.toDurationList;
import static ome.smuggler.core.convert.RawConfigValues.toPath;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.items.ImportConfig;

/**
 * Implements {@link ImportConfigSource} by extracting and validating values 
 * from an underlying {@link ImportConfig}.
 */
public class ImportConfigReader implements ImportConfigSource {
    
    private final Path importLogDir;
    private final Duration logRetentionPeriod;
    private final List<Duration> retryIntervals;
    private final Path failedImportLogDir;
    
    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration 
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportConfigReader(ImportConfig config) {
        requireNonNull(config, "config");
        
        importLogDir = toPath(config.getImportLogDir());
        logRetentionPeriod = toDuration(config.getLogRetentionMinutes());
        retryIntervals = toDurationList(config.getRetryIntervals());
        failedImportLogDir = toPath(config.getFailedImportLogDir());
    }
    
    @Override
    public Path importLogDir() {
        return importLogDir;
    }

    @Override
    public Duration logRetentionPeriod() {
        return logRetentionPeriod;
    }

    @Override
    public List<Duration> retryIntervals() {
        return retryIntervals;
    }

    @Override
    public Path failedImportLogDir() {
        return failedImportLogDir;
    }

}
