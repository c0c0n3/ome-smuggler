package ome.smuggler.core.service.impl;

import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.service.impl.ImportOutputFormatter.*;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;


public class ImportOutput {
    
    private final ImportConfig logCfg;
    private final QueuedImport task;
    private final ImportLogPath outputFile;
    
    public ImportOutput(ImportConfig logCfg, QueuedImport task) {
        requireNonNull(logCfg, "logCfg");
        requireNonNull(task, "task");
        
        this.logCfg = logCfg;
        this.task = task;
        this.outputFile = outputFile(task);
    }

    private ImportLogPath outputFile(QueuedImport task) {
        Path dir = Paths.get(logCfg.getImportLogDir());
        return new ImportLogPath(dir, task.getTaskId());
    }
    
    private void output(String line) throws IOException {
        List<String> content = Arrays.asList(line);
        Files.write(outputPath(), content, CREATE, WRITE, APPEND);
    }
    
    public Path outputPath() {
        return outputFile.get();
    }
    
    public ImportLogPath importLogPath() {
        return outputFile;
    }
    
    public void writeQueued() throws IOException {
        output(queued(task));
    }
    
    public void writeHeader(ImporterCommandBuilder importCommand) 
            throws IOException {
        if (Files.exists(outputPath())) {
            Files.delete(outputPath());
        }
        output(header(task, importCommand, outputPath()));
    }
    
    public void writeFooter(boolean success, int exitStatus) throws IOException {
        output(footer(success, exitStatus));
    }
    
    public void writeFooter(Exception e) {
        try {
            output(footer(e));
        } catch (IOException ioe) {
            throwAsIfUnchecked(ioe);
        }
    }
    
}
