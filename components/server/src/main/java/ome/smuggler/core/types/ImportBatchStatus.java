package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Tracks progress of an {@link ImportBatch}.
 */
public class ImportBatchStatus {

    private final ImportBatch batch;
    private final Set<ImportId> succeeded;
    private final Set<ImportId> failed;

    /**
     * Creates a new instance.
     * @param batch the batch to track.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportBatchStatus(ImportBatch batch) {
        requireNonNull(batch, "batch");

        this.batch = batch;
        this.succeeded = new HashSet<>();
        this.failed = new HashSet<>();
    }

    private ImportId checkImportIsInBatch(QueuedImport importInBatch) {
        requireNonNull(importInBatch, "importInBatch");

        ImportId id = importInBatch.getTaskId();
        boolean in = batch.imports()
                          .map(QueuedImport::getTaskId)
                          .anyMatch(x -> x.equals(id));
        if (!in) {
            throw new IllegalArgumentException("import not in batch: " + id);
        }
        return id;
    }

    private Set<QueuedImport> collect(Set<ImportId> qs) {
        return batch.imports()
                    .filter(q -> qs.contains(q.getTaskId()))
                    .collect(toSet());
    }

    /**
     * @return the batch we're tracking.
     */
    public ImportBatch batch() {
        return batch;
    }

    /**
     * Marks the specified import in the batch as completed successfully.
     * @param importInBatch the import.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the specified import is not that of
     * an import in the batch or it has already been
     * {@link #addToFailed(QueuedImport) marked as failed}.
     * @see #addToFailed(QueuedImport) addToFailed
     */
    public void addToSucceeded(QueuedImport importInBatch) {
        ImportId id = checkImportIsInBatch(importInBatch);
        if (failed.contains(id)) {
            throw new IllegalArgumentException(
                    "already marked as failed: " + id);
        }

        succeeded.add(id);
    }

    /**
     * Marks the specified import in the batch as terminated with a failure.
     * @param importInBatch the import.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the specified import is not that of
     * an import in the batch or it has already been
     * {@link #addToSucceeded(QueuedImport) marked as succeeded}.
     * @see #addToSucceeded(QueuedImport) addToSucceeded
     */
    public void addToFailed(QueuedImport importInBatch) {
        ImportId id = checkImportIsInBatch(importInBatch);
        if (succeeded.contains(id)) {
            throw new IllegalArgumentException(
                    "already marked as succeeded: " + id);
        }

        failed.add(id);
    }

    /**
     * Have all the imports in this batch been processed?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean allProcessed() {
        return batch.imports().count() == (succeeded.size() + failed.size());
    }

    /**
     * @return the imports in the batch that completed successfully.
     */
    public Set<QueuedImport> succeeded() {
        return collect(succeeded);
    }

    /**
     * @return the imports in the batch that terminated with a failure.
     */
    public Set<QueuedImport> failed() {
        return collect(failed);
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportBatchStatus) {
            ImportBatchStatus other = (ImportBatchStatus) x;
            return this.batch.equals(other.batch)
                && this.succeeded.equals(other.succeeded)
                && this.failed.equals(other.failed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, succeeded, failed);
    }

}
