package integration.web;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import ome.smuggler.core.io.FileOps;
import ome.smuggler.core.service.file.impl.TaskIdPathStore;
import ome.smuggler.core.types.BaseStringId;

/**
 * A pre-populated task store in a temporary directory.
 * The content of each task file is the task ID.
 */
public class TempDirTaskStore extends TaskIdPathStore<BaseStringId> {
    
    public TempDirTaskStore(int initialTaskFiles) throws IOException {
        super(Files.createTempDirectory("smuggler-tests-task-store"), 
              BaseStringId::new);
        populate(initialTaskFiles);
    }
    
    private void populate(int initialTaskFiles) {
        for (int k = 0; k < initialTaskFiles; ++k) {
            writeNewTaskFile();
        }
    }
    
    private void writeNewTaskFile() {
        BaseStringId taskId = new BaseStringId();
        FileOps.writeNew(pathFor(taskId), out -> {
            PrintStream writer = new PrintStream(out);
            writer.print(taskId);
        });
    }
    
}
