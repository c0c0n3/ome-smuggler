package ome.smuggler.core.service.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;
import java.time.Duration;

import ome.smuggler.config.items.CliImporterConfig;
import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.ImportProcessor;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportLogFile;
import ome.smuggler.core.types.QueuedImport;

public class ImportRunner implements ImportProcessor {

    private final CliImporterConfig cliCfg;
    private final ImportLogConfig logCfg;
    private final SchedulingSource<ImportLogFile> gcQueue;
    
    
    public ImportRunner(CliImporterConfig cliCfg, ImportLogConfig logCfg,
            SchedulingSource<ImportLogFile> gcQueue) {
        requireNonNull(cliCfg, "cliCfg");
        requireNonNull(logCfg, "logCfg");
        
        this.cliCfg = cliCfg;
        this.logCfg = logCfg;
        this.gcQueue = gcQueue;
    }
    
    private void scheduleDeletion(ImportLogFile logFile) {
        long fromNow = logCfg.getRetentionMinutes();
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(fromNow));
        gcQueue.uncheckedSend(when, logFile);
    }
    
    @Override
    public void consume(QueuedImport task) {
        ImporterCommandBuilder cliOmeroImporter = 
                new ImporterCommandBuilder(cliCfg, task.getRequest());
        CommandRunner runner = new CommandRunner(cliOmeroImporter);
        ImportOutput output = new ImportOutput(logCfg, task);
        ImportLogFile logFile = new ImportLogFile(output.importLogPath());
        
        try {
            output.writeHeader(cliOmeroImporter);
            int status = runner.exec(output.outputPath());
            output.writeFooter(status == 0, status);
        } catch (IOException | InterruptedException e) {
            output.writeFooter(e);
            throwAsIfUnchecked(e);
        }
        scheduleDeletion(logFile);
    }

}
