package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import ome.smuggler.core.io.CommandRunner;
import ome.smuggler.core.io.StreamOps;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.core.types.Schedule;
import util.object.Pair;

/**
 * Implementation of the {@link ImportTracker import tracking} service.
 */
public class ImportMonitor implements ImportTracker {

    private final ImportEnv env;
    private final ImportKeepAliveScheduler scheduler;
    
    /**
     * Creates a new instance.
     * @param env the import environment to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportMonitor(ImportEnv env) {
        requireNonNull(env, "env");
        
        this.env = env;
        this.scheduler = new ImportKeepAliveScheduler(
                                            env.config().keepAliveInterval());
    }
    
    private void pingOmero(QueuedImport task) {
        KeepAliveCommandBuilder keepAlive = new KeepAliveCommandBuilder(
                env.cliConfig(), task.getRequest());
        CommandRunner cmd = new CommandRunner(keepAlive);
        try {
            Pair<Integer, String> output =
                    cmd.exec(StreamOps::readLinesIntoString);
            int exitCode = output.fst();
            String cmdOutput = output.snd();

            if (exitCode == 0) {
                env.log().keepAlive().successful(task, exitCode, cmdOutput,
                        keepAlive);
            } else {
                env.log().keepAlive().failed(task, exitCode, cmdOutput,
                        keepAlive);
            }
        } catch (Exception e) {
            env.log().keepAlive().failed(task, e);
        }
    }
    
    @Override
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return env.importLogPathFor(taskId);
    }

    @Override
    public Optional<Schedule<ImportKeepAlive>> consume(
            CountedSchedule current, ImportKeepAlive data) {
        Optional<Schedule<ImportKeepAlive>> next = 
                scheduler.nextSchedule(current, data);
        if (next.isPresent()) {
            pingOmero(data.importRequest());
        }
        return next;
    }
    
}
