package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.service.imports.ImportDisposer;
import ome.smuggler.core.types.ProcessedImport;

/**
 * Implements the {@link ImportDisposer}.
 */
public class ImportDisposeAction implements ImportDisposer {

    private final ImportEnv env;

    public ImportDisposeAction(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    @Override
    public void dispose(ProcessedImport task) {
        requireNonNull(task, "task");

        Path p = env.importLogPathFor(task.get().getTaskId()).get();
        FileOps.delete(p);
    }

}
