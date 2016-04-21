package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.types.ImportInput;

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
        OmeCliCommandRunner runner =
                new OmeCliCommandRunner(env, cliOmeroImporter);
        return runner.run(importLog);
    }

}
