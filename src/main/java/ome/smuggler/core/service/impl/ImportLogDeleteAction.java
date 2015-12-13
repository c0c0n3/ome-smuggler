package ome.smuggler.core.service.impl;

import java.nio.file.Path;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.service.ImportLogDisposer;
import ome.smuggler.core.types.ImportLogFile;

public class ImportLogDeleteAction implements ImportLogDisposer {

    @Override
    public void dispose(ImportLogFile expiredFile) {
        Path p = expiredFile.get().toPath();
        FileOps.delete(p);
    }

}
