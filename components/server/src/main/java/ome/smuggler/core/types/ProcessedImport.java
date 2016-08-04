package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import util.object.Wrapper;

/**
 * Same as a {@link QueuedImport} but only used after the import task has
 * completed and needs to be garbage-collected.
 */
public class ProcessedImport extends Wrapper<QueuedImport> {

    /**
     * Creates a new instance.
     * @param task the completed import.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ProcessedImport(QueuedImport task) {
        super(task);
        requireNonNull(task, "task");
    }

}
/* NOTE. Spring beans.
 * We only need this class because we create a QChannelFactory<QueuedImport>
 * bean (in ImportQBeans) but we'd also need another instance of the same
 * bean (i.e. another QChannelFactory<QueuedImport>) in ImportGcBeans. Which
 * would confuse the hell out of Spring, so, in ImportGcBeans, we create a
 * QChannelFactory<ProcessedImport> instead.
 */
