package ome.smuggler.config.data;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.types.ValueParserFactory.email;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.MailConfigSource;

/**
 * Dev mail settings.
 */
public class DevMailConfigSource implements MailConfigSource {

    private final Path baseDataDir;
    
    public DevMailConfigSource(Path baseDataDir) {
        requireNonNull(baseDataDir, "baseDataDir");
        this.baseDataDir = baseDataDir;
    }
    
    @Override
    public Email fromAddress() {
        return email("noreply@openmicroscopy.org").getRight();
    }

    @Override
    public URI mailServer() {
        return URI.create("smtp://localhost:25");
    }

    @Override
    public List<Duration> retryIntervals() {
        return Collections.emptyList();
    }

    @Override
    public Path deadMailDir() {
        return baseDataDir.resolve(MailYmlFile.DeadMailDirRelPath);
    }
    
}