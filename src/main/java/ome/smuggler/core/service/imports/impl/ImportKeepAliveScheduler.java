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
    /* TODO keep-alive party pooper.
     * The code and explanation above assume that:
     * 
     *  - the first keep-alive message ever delivered for an import task
     *    has count = 1.
     *  
     * Unfortunately, that may not always be the case. In fact, here are two
     * nasty scenarios where the assumption doesn't hold true.
     * The Initial keep-alive message m1 with count 1 is put on the queue by the
     * ImportTrigger but it sits on the queue until after the ImportRunner
     * completes the import and puts the "stop" message m2 with count 2 on the
     * queue. Now HornetQ delivers m2 before m1 because:
     * 
     * 1. In general, there are no message ordering guarantees; or
     * 2. HornetQ crashed and on reboot the messages are picked up from the 
     * client buffer and delivered out of order.
     * 
     * More details here:
     * - http://stackoverflow.com/questions/4085270/force-order-of-messages-with-hornetq
     * 
     * In both cases, the keep-alive would go on forever because the scheduler
     * would keep on repeating the schedule.
     * Though both scenarios are unlikely, they're possible.
     * 
     * Suggested solutions:
     * --------------------
     * 1. Ditch the keep-alive functionality. Rather use long-lived sessions.
     * 2. Change keep-alive implementation:
     *  (A) Keep on scheduling OMERO pings for a configured period of time (e.g.
     *      a week) then stop. 
     *  (B) Or close the session on import completion and keep on scheduling 
     *      pings as long as the keep-alive command does not return an exit code
     *      that indicates the session is closed.
     *  (C) Or a combination of (A) and (B).   
     */
}
