package ome.smuggler.core.types;

import java.io.File;

import util.object.Wrapper;

/**
 * Wrapper for the {@link File} obtained from a {@link ImportLogPath}.
 */
public class ImportLogFile extends Wrapper<File> {

    public ImportLogFile(ImportLogPath filePath) {
        super(filePath.get().toFile());
    }

}
/* NOTE. Gson serialization issues.
 * Even though conceptually redundant, we need this class to avoid a nasty
 * stack overflow caused by Gson when you serialize an ImportLogPath---think
 * the reason is that ImportLogPath wraps a Path which is an interface. This
 * is then the reason why the import GC queue's type param is ImportLogFile
 * instead of ImportLogPath. Another option would be to make the GC queue's
 * type param File or String but I'm not too keen on that as it'd be less
 * type-safe as the fact we're handling an import log file (not just any file)
 * would not be noted by the type system.
 */