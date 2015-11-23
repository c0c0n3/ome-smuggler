package ome.smuggler.core.service.impl;

import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ome.smuggler.core.service.ImportLogDisposer;
import ome.smuggler.core.types.ImportLogFile;

public class ImportLogDeleteAction implements ImportLogDisposer {

    @Override
    public void dispose(ImportLogFile expiredFile) {
        Path p = expiredFile.get().toPath();
        if (Files.exists(p)) {
            try {
                Files.delete(p);
            } catch (IOException e) {
                throwAsIfUnchecked(e);
            }
        }
    }

}
