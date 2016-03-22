package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportKeepAlive;
import ome.smuggler.core.types.Schedule;

/**
 * Encapsulates the logic to decide for how long to keep on scheduling an OMERO 
 * session keep-alive.
 */
public class ImportKeepAliveScheduler {

    private final Set<ImportId> queuedImports;
    private final Duration keepAliveInterval;
    
    public ImportKeepAliveScheduler(Duration keepAliveInterval) {
        requireNonNull(keepAliveInterval, "keepAliveInterval");
        
        this.keepAliveInterval = keepAliveInterval;
        this.queuedImports = Collections.synchronizedSet(new HashSet<>());
    }
    
    private Optional<Schedule<ImportKeepAlive>> reschedule(
            ImportKeepAlive data) {
        FutureTimepoint when = new FutureTimepoint(keepAliveInterval);
        return Optional.of(new Schedule<>(when, data));
    }
    
    /**
     * Decides if a new session keep-alive should be scheduled after the 
     * current one.
     * @param current the current delivery count. 
     * @param data the current request to keep the session alive.
     * @return either a new schedule or empty if the session doesn't need a
     * further keep-alive.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public Optional<Schedule<ImportKeepAlive>> nextSchedule(
            CountedSchedule current, ImportKeepAlive data) {
        requireNonNull(current, "current");
        requireNonNull(data, "data");
        
        ImportId taskId = data.importRequest().getTaskId();
        if (data.stop()) {
            queuedImports.remove(taskId);
        } else {
            if (current.count().get() == 1) {
                queuedImports.add(taskId);
            }
            if (queuedImports.contains(taskId)) {  // (*)
                return reschedule(data);
            }
        }
        return Optional.empty();
    }
    /* (*) The stop request may be delivered before a regular keep alive, which
     * explains the convoluted logic in this method. 
     * In fact, it could happen that the import runner puts the stop request on
     * the queue while there's still a scheduled keep-alive in the queue that
     * is scheduled for delivery after the stop request---the stop request has
     * a schedule of "now", see the import runner.
     * Or it could happen happen that there's no scheduled keep-alive on the
     * queue as it's being processed by a consumer thread c1 but c1 is suspended
     * and the import runner puts the stop on the queue; another consumer c2
     * picks it up before c1 is resumed...
     */
}
