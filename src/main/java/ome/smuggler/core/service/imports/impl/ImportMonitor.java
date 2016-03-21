package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.Schedule;

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
    
    private void pingOmero(String sessionKey) {
        // TODO implement
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
            pingOmero(data.importRequest().getRequest().getSessionKey());
        }
        return next;
    }
    
}
