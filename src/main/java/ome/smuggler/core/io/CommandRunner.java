package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.List;

import util.runtime.CommandBuilder;

/**
 * Utility to run an external process redirecting both its error and output
 * streams to a file.
 */
public class CommandRunner {

    private static List<String> commandLine(CommandBuilder command) {
        return command.tokens().collect(toList());
    }
    
    private static ProcessBuilder processBuilder(CommandBuilder command) {
        return new ProcessBuilder(commandLine(command));
    }
    
    private final CommandBuilder command;
    
    /**
     * Creates a new instance to run a given process.
     * @param command the command to start the process.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CommandRunner(CommandBuilder command) {
        requireNonNull(command, "command");
        this.command = command;
    }
    
    private Process startProcess(Path outputFile) throws IOException {
        ProcessBuilder builder = processBuilder(command);
        builder.redirectErrorStream(true);
        builder.redirectOutput(Redirect.appendTo(outputFile.toFile()));
        
        Process process = builder.start();
        process.getOutputStream().close();
        
        return process;
    }
    
    /**
     * Runs the process redirecting both its error and output streams to the
     * specified file.
     * @param outputFile the file to redirect the process's streams to.
     * @return the process's exit status.
     * @throws IOException if an I/O error occurs while running the process.
     * @throws InterruptedException if the current thread is interrupted before 
     * the spawned process terminates.
     */
    public int exec(Path outputFile) throws IOException, InterruptedException {
        requireNonNull(outputFile, "outputFile");
        
        int status = startProcess(outputFile).waitFor();
        
        return status;
    }
    
}
