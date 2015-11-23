package ome.smuggler.core.types;

import java.io.File;

import util.object.Wrapper;

public class ImportLogFile extends Wrapper<File> {

    public ImportLogFile(ImportLogPath filePath) {
        super(filePath.get().toFile());
    }

}
