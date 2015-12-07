package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static ome.smuggler.core.msg.RepeatAction.Stop;
import static ome.smuggler.core.service.impl.Loggers.logTransientError;

import java.io.IOException;
import java.time.Duration;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;

public class ImportRunner implements ImportProcessor {

    private final CliImporterConfig cliCfg;
    private final ImportConfig logCfg;
    private final SchedulingSource<ImportLogFile> gcQueue;
    
    
    public ImportRunner(CliImporterConfig cliCfg, ImportConfig logCfg,
            SchedulingSource<ImportLogFile> gcQueue) {
        requireNonNull(cliCfg, "cliCfg");
        requireNonNull(logCfg, "logCfg");
        
        this.cliCfg = cliCfg;
        this.logCfg = logCfg;
        this.gcQueue = gcQueue;
    }
    
    private void scheduleDeletion(ImportLogFile logFile) {
        long fromNow = logCfg.getLogRetentionMinutes();
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(fromNow));
        gcQueue.uncheckedSend(message(when, logFile));
    }
    
    @Override
    public RepeatAction consume(QueuedImport task) {
        ImporterCommandBuilder cliOmeroImporter = 
                new ImporterCommandBuilder(cliCfg, task.getRequest());
        CommandRunner runner = new CommandRunner(cliOmeroImporter);
        ImportOutput output = new ImportOutput(logCfg, task);
        ImportLogFile logFile = new ImportLogFile(output.importLogPath());
        
        try {
            output.writeHeader(cliOmeroImporter);
            int status = runner.exec(output.outputPath());
            boolean succeeded = status == 0;
            
            output.writeFooter(succeeded, status);
            return succeeded ? Stop : Repeat;
        } catch (IOException | InterruptedException e) {
            output.writeFooter(e);
            logTransientError(this, e);
            return Repeat;
        } finally {
            scheduleDeletion(logFile);
        }
    }

}
