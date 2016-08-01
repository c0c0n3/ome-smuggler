package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Set;
import java.util.stream.IntStream;


@RunWith(Theories.class)
public class ImportBatchStatusTest {

    static final int numImports = 4;

    static void markCompletions(ImportBatchStatus status,
                                int nSuccess, int nFails) {
        status.batch().imports().limit(nSuccess)
                .forEach(status::addToSucceeded);
        status.batch().imports().skip(nSuccess).limit(nFails)
                .forEach(status::addToFailed);
    }

    static boolean isPartition(int x, int y) {
        return x + y == numImports;
    }

    static boolean isBelowPartition(int x, int y) {
        return x + y < numImports;
    }

    @DataPoints
    public static int[] genSupply =
            IntStream.rangeClosed(0, numImports).toArray();


    private ImportBatchStatus target;

    @Before
    public void setup() {
        String[] files = IntStream.rangeClosed(1, numImports)
                                  .mapToObj(n -> "file/" + n)
                                  .toArray(String[]::new);
        ImportBatch batch = ImportBatchTest.makeNew(files);
        target = new ImportBatchStatus(batch);
    }

    private QueuedImport importNotInBatch() {
        return new QueuedImport(new ImportId(),
                                ImportBatchTest.makeNewImportInput());
    }

    void assertSuccessAndFailSetsAreDisjoint(int nSuccess, int nFails) {
        Set<QueuedImport> succeeded = target.succeeded(),
                            failed = target.failed();

        assertThat(succeeded.size(), is(nSuccess));
        assertThat(failed.size(), is(nFails));

        succeeded.retainAll(failed);  // intersection now in succeeded
        assertThat(succeeded.size(), is(0));

    }

    @Theory
    public void allProcessedTrueIfAllImportsBeenAdded(int nSuccess, int nFails) {
        assumeTrue(isPartition(nSuccess, nFails));

        markCompletions(target, nSuccess, nFails);

        assertTrue(target.allProcessed());
        assertSuccessAndFailSetsAreDisjoint(nSuccess, nFails);
    }

    @Theory
    public void allProcessedFalseIfSomeImportsNotBeenAdded(int nSuccess, int nFails) {
        assumeTrue(isBelowPartition(nSuccess, nFails));

        markCompletions(target, nSuccess, nFails);

        assertFalse(target.allProcessed());
        assertSuccessAndFailSetsAreDisjoint(nSuccess, nFails);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new ImportBatchStatus(null);
    }

    @Test (expected = NullPointerException.class)
    public void addToSucceededThrowsIfNullArg() {
        target.addToSucceeded(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToSucceededThrowsIfImportNotInBatch() {
        target.addToSucceeded(importNotInBatch());
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToSucceededThrowsIfImportAlreadyAddedToFailed() {
        QueuedImport qi = target.batch().imports().findFirst().get();
        target.addToFailed(qi);
        target.addToSucceeded(qi);
    }

    public void canReAddSameImportToSucceededSet() {
        QueuedImport qi = target.batch().imports().findFirst().get();
        target.addToSucceeded(qi);
        target.addToSucceeded(qi);

        assertThat(target.succeeded().size(), is(1));
        assertThat(target.failed().size(), is(0));
        assertTrue(target.succeeded().contains(qi));
    }

    @Test (expected = NullPointerException.class)
    public void addToFailedThrowsIfNullArg() {
        target.addToFailed(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToFailedThrowsIfImportNotInBatch() {
        target.addToFailed(importNotInBatch());
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToFailedThrowsIfImportAlreadyAddedToSucceeded() {
        QueuedImport qi = target.batch().imports().findFirst().get();
        target.addToSucceeded(qi);
        target.addToFailed(qi);
    }

    public void canReAddSameImportToFailedSet() {
        QueuedImport qi = target.batch().imports().findFirst().get();
        target.addToFailed(qi);
        target.addToFailed(qi);

        assertThat(target.failed().size(), is(1));
        assertThat(target.succeeded().size(), is(0));
        assertTrue(target.failed().contains(qi));
    }
}
