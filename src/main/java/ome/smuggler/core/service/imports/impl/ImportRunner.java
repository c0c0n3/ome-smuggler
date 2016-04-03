package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static ome.smuggler.core.msg.RepeatAction.Stop;

import java.io.IOException;
import java.nio.file.Path;

import ome.smuggler.core.io.CommandRunner;
import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.QueuedImport;


public class ImportRunner implements ImportProcessor {

    private final ImportEnv env;
    
    public ImportRunner(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    private int run(ImporterCommandBuilder cliOmeroImporter, QueuedImport task) 
            throws IOException, InterruptedException {
        CommandRunner runner = new CommandRunner(cliOmeroImporter);
        Path importTarget = env.importLogPathFor(task.getTaskId()).get(); 
        return runner.exec(importTarget);
    }
    
    @Override
    public RepeatAction consume(QueuedImport task) {
        env.log().importStart(task);
        
        ImporterCommandBuilder cliOmeroImporter = 
                new ImporterCommandBuilder(env.cliConfig(), task.getRequest());
        ImportOutput output = new ImportOutput(
                env.importLogPathFor(task.getTaskId()), task);
        
        RepeatAction action = Repeat;
        try {
            output.writeHeader(cliOmeroImporter);
            int status = run(cliOmeroImporter, task); 
            
            boolean succeeded = status == 0;
            output.writeFooter(succeeded, status);
            
            if (succeeded) {
                new ImportOutcomeNotifier(env, task).tellSuccess();
                
                env.log().importSuccessful(task);
                action = Stop;
            } 
        } catch (IOException | InterruptedException e) {
            output.writeFooter(e);
            env.log().transientError(this, e);
        } finally {
            if (Stop.equals(action)) {
                env.garbageCollector().run(task);
            }
        }
        return action;
    }

}
