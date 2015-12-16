package ome.smuggler.config.data;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import ome.smuggler.core.types.ImportConfigSource;

/**
 * Dev import settings.
 */
public class DevImportConfigSource implements ImportConfigSource {

    private final Path baseDataDir;
    
    public DevImportConfigSource(Path baseDataDir) {
        requireNonNull(baseDataDir, "baseDataDir");
        this.baseDataDir = baseDataDir;
    }
    
    @Override
    public Path importLogDir() {
        return baseDataDir.resolve(ImportYmlFile.RelImportLogDirPath);
    }

    @Override
    public Duration logRetentionPeriod() {
        return Duration.ofSeconds(5);
    }

    @Override
    public List<Duration> retryIntervals() {
        return Collections.emptyList();
    }

    @Override
    public Path failedImportLogDir() {
        return baseDataDir.resolve(ImportYmlFile.RelFailedImportLogDirPath);
    }

}
