package ome.smuggler.core.service.impl;

import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.service.impl.ImportOutputFormatter.*;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;


public class ImportOutput {
    
    private final QueuedImport task;
    private final Path outputFile;
    
    public ImportOutput(ImportLogPath outputFile, QueuedImport task) {
        requireNonNull(outputFile, "outputFile");
        requireNonNull(task, "task");
        
        this.task = task;
        this.outputFile = outputFile.get();
    }

    private void output(String line) throws IOException {
        List<String> content = Arrays.asList(line);
        Files.write(outputFile, content, CREATE, WRITE, APPEND);
    }
        
    public void writeQueued() throws IOException {
        output(queued(task));
    }
    
    public void writeHeader(ImporterCommandBuilder importCommand) 
            throws IOException {
        output(header(task, importCommand, outputFile));
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
