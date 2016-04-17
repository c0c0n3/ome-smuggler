package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.convert.RawConfigValues.toDuration;
import static ome.smuggler.core.convert.RawConfigValues.toDurationList;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.items.ImportConfig;

/**
 * Implements {@link ImportConfigSource} by extracting and validating values 
 * from an underlying {@link ImportConfig}.
 */
public class ImportConfigReader implements ImportConfigSource {
    
    public static final Duration DefaultKeepAliveInterval = toDuration(5L);
    
    private final Path importLogDir;
    private final Duration logRetentionPeriod;
    private final List<Duration> retryIntervals;
    private final Path failedImportLogDir;
    private final Duration keepAliveInterval;
    
    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration 
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportConfigReader(ImportConfig config) {
        requireNonNull(config, "config");
        
        BaseDataDir base = new BaseDataDir();
        
        importLogDir = base.resolveRequiredPath(config.getImportLogDir());
        logRetentionPeriod = toDuration(config.getLogRetentionMinutes());
        retryIntervals = toDurationList(config.getRetryIntervals());
        failedImportLogDir = base.resolveRequiredPath(
                                config.getFailedImportLogDir());
        keepAliveInterval = toDuration(config.getKeepAliveInterval(),
                                       DefaultKeepAliveInterval);
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

    @Override
    public Duration keepAliveInterval() {
        return keepAliveInterval;
    }

}
