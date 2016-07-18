package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportInput;

import java.nio.file.Path;

/**
 * OMERO import service implementation.
 */
public class Importer implements ImportService {

    private final OmeroEnv env;
    private final ImportConfigSource cfg;

    /**
     * Creates a new instance.
     * @param env the service environment.
     */
    public Importer(OmeroEnv env, ImportConfigSource cfg) {
        requireNonNull(env, "env");
        requireNonNull(cfg, "cfg");

        this.env = env;
        this.cfg = cfg;
    }

    @Override
    public boolean run(ImportInput data, Path importLog) {
        ImporterCommandBuilder cliOmeroImporter =
                new ImporterCommandBuilder(env.config(),
                        data, cfg.niceCommand());
        OmeCliCommandRunner runner =
                new OmeCliCommandRunner(env, cliOmeroImporter);
        return runner.run(importLog);
    }

}
