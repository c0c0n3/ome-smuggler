package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.io.CommandRunner;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.types.ImportInput;

import java.io.IOException;
import java.nio.file.Path;

/**
 * OMERO import service implementation.
 */
public class Importer implements ImportService {

    private final OmeroEnv env;

    /**
     * Creates a new instance.
     * @param env the service environment.
     */
    public Importer(OmeroEnv env) {
        requireNonNull(env, "env");

        this.env = env;
    }

    @Override
    public boolean importData(ImportInput data, Path importLog) {
        ImporterCommandBuilder cliOmeroImporter =
                new ImporterCommandBuilder(env.config(), data);
        CommandRunner runner = new CommandRunner(cliOmeroImporter);
        try {
            int status = runner.exec(importLog);
            if (status == 0) {
                // TODO: log
                return true;
            } else {
                // TODO: log
                return false;
            }
        } catch (IOException | InterruptedException e) {
            env.log().transientError(this, e);
            return false;
        }
    }

}
