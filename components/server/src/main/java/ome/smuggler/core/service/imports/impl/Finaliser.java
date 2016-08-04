package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runAndCatch;

import ome.smuggler.core.types.QueuedImport;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Carries out completion procedures after the {@link ImportRunner} or the
 * {@link ImportFailureHandler} have finished their work.
 * These include notifying the user of the import outcome and cleaning up
 * allocated resources.
 */
public class Finaliser {

    /**
     * Completes the import task when the OMERO import has run successfully.
     * @param env the import environment.
     * @param task the import task.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void onSuccess(ImportEnv env, QueuedImport task) {
        new Finaliser(env, task).handleSuccess();
    }

    /**
     * Completes the import task when the OMERO import has failed.
     * @param env the import environment.
     * @param task the import task.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void onFailure(ImportEnv env, QueuedImport task) {
        new Finaliser(env, task).handleFailure();
    }

    private final ImportEnv env;
    private final QueuedImport task;

    private Finaliser(ImportEnv env, QueuedImport task) {
        requireNonNull(env, "env");
        requireNonNull(task, "task");

        this.env = env;
        this.task = task;
    }

    private void handleSuccess() {
        env.log().importSuccessful(task);
        runFinalisationTasks(ImportOutcomeNotifier::tellSuccess);
    }

    private void handleFailure() {
        env.log().importPermanentFailure(task);
        runFinalisationTasks(ImportOutcomeNotifier::tellFailure);
    }

    private void runFinalisationTasks(Consumer<ImportOutcomeNotifier> f) {
        Optional<Throwable>[] maybeE = runAndCatch(
                () -> env.garbageCollector().run(task),
                () -> f.accept(new ImportOutcomeNotifier(env, task)));

        logAnyExceptions(maybeE);
    }

    private void logAnyExceptions(Optional<Throwable>[] maybeE) {
        Stream.of(maybeE)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .forEach(t -> env.log().transientError(this, t));
    }

}
