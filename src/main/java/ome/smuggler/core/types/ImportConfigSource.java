package ome.smuggler.core.types;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface ImportConfigSource {

    Path importLogDir();
    
    Duration logRetentionPeriod();
    
    List<Duration> retryIntervals();
    
    Path failedImportLogDir();
    
}
