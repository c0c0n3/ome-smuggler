package ome.smuggler.core.types;

import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

public interface ImportConfigSource {

    Path importLogDir();
    
    Duration logRetentionPeriod();
    
    Stream<Duration> retryIntervals();
    
    Path failedImportLogDir();
    
}
