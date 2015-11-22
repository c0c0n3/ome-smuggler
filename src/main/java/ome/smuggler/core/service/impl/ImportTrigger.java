package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;

import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.ImportRequestor;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

public class ImportTrigger implements ImportRequestor {

    private final ChannelSource<QueuedImport> queue; 
    private final ImportLogConfig logConfig;
    
    public ImportTrigger(ChannelSource<QueuedImport> queue, 
                         ImportLogConfig logConfig) {
        requireNonNull(queue, "queue");
        requireNonNull(logConfig, "logConfig");
        
        this.queue = queue;
        this.logConfig = logConfig;
    }
    
    private void notifyQueued(QueuedImport task) {
        ImportOutput out = new ImportOutput(logConfig, task);
        try {
            out.writeQueued();
        } catch (IOException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    @Override
    public ImportId enqueue(ImportInput request) {
        ImportId taskId = new ImportId();
        QueuedImport task = new QueuedImport(taskId, request);
        notifyQueued(task);
        queue.uncheckedSend(task);
        
        return taskId;
    }

}
