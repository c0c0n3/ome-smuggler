package ome.smuggler.core.service.impl;

import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.throwAsIfUnchecked;
import static util.string.Strings.write;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.config.items.ImportLogConfig;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;

public class ImportOutput {

    private static void ensureDirectories(Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());
    }
    
    private final ImportLogConfig logCfg;
    private final QueuedImport task;
    private final Path outputFile;
    
    public ImportOutput(ImportLogConfig logCfg, QueuedImport task) {
        requireNonNull(logCfg, "logCfg");
        requireNonNull(task, "task");
        
        this.logCfg = logCfg;
        this.task = task;
        this.outputFile = outputFile(task);
    }

    private Path outputFile(QueuedImport task) {
        Path dir = Paths.get(logCfg.getImportLogDir());
        return new ImportLogPath(dir, task.getTaskId()).get();
    }
    
    private String queued() {
        return write(out -> {
            ImportInput req = task.getRequest();
            out.println("====================> Queued OMERO Import:");
            out.format("Import ID: %s%n", task.getTaskId());
            out.format("Import Target: %s%n", req.getTarget());
            out.format("OMERO Server: %s%n", req.getOmeroHost());
            out.format("Experimenter's Email: %s%n", req.getExperimenterEmail());
            out.println("============================================");
        });
    }
    
    private String header(ImporterCommandBuilder importCommand) {
        return write(out -> {
            ImportInput req = task.getRequest();
            out.println("====================> Starting OMERO Import:");
            out.format("Import ID: %s%n", task.getTaskId());
            out.format("Import Target: %s%n", req.getTarget());
            out.format("OMERO Server: %s%n", req.getOmeroHost());
            out.format("Experimenter's Email: %s%n", req.getExperimenterEmail());
            out.format("Import Command: %s%n", importCommand);
            out.format("Output File: %s%n", outputFile);
            out.println("============================================");
        });
    }
    
    private String footer(boolean success, int exitStatus) {
        return write(out -> {
            out.println();
            out.println("=================> OMERO Import Run Report:");
            out.format("Succeeded: %s%n", success);
            out.format("Exit Status: %s%n", exitStatus);
            out.println("============================================");
        });
    }
    
    private String footer(Exception e) {
        return write(out -> {
            out.println();
            out.println("=================> OMERO Import Run Report:");
            out.format("Succeeded: %s%n", false);
            out.format("Cause of Failure: %s%n", e.getMessage());
            out.println("Error Detail:");
            e.printStackTrace(out);
            out.println("============================================");
        });
    }
    
    private void output(String line) throws IOException {
        ensureDirectories(outputFile);
        List<String> content = Arrays.asList(line);
        Files.write(outputFile, content, CREATE, WRITE, APPEND);
    }
    
    public Path outputPath() {
        return outputFile;
    }
    
    public void writeQueued() throws IOException {
        output(queued());
    }
    
    public void writeHeader(ImporterCommandBuilder importCommand) 
            throws IOException {
        Files.delete(outputFile);
        output(header(importCommand));
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
