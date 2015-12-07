package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ome.smuggler.config.items.ImportConfig;

public class ImportConfigReader implements ImportConfigSource {

    private static Duration toDuration(Long minutes) {
        PositiveN v = PositiveN.of(minutes);  // NB throws if minutes <= 0
        return Duration.ofMinutes(v.get());
    }
    
    private static List<Duration> toDurationList(Long[] minutes) {
        return Stream.of(minutes == null ? new Long[0] : minutes)
                     .map(ms -> toDuration(ms))
                     .collect(collectingAndThen(
                                 toList(), Collections::unmodifiableList));
    }
    
    private final Path importLogDir;
    private final Duration logRetentionPeriod;
    private final List<Duration> retryIntervals;
    private final Path failedImportLogDir;
    
    public ImportConfigReader(ImportConfig config) {
        requireNonNull(config, "config");
        
        importLogDir = Paths.get(config.getImportLogDir());
        logRetentionPeriod = toDuration(config.getLogRetentionMinutes());
        retryIntervals = toDurationList(config.getRetryIntervals());
        failedImportLogDir = Paths.get(config.getFailedImportLogDir());
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
