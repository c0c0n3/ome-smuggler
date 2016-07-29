package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.unchecked;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.BaseStringId;


public class KeyValueFileStoreTest {

    @Rule
    public final TemporaryFolder storeDir = new TemporaryFolder();

    private TaskFileStore<BaseStringId> store;
    private KeyValueStore<BaseStringId, Integer> target;


    private Integer reader(InputStream in) throws Exception {
        return in.read();
    }

    private void writer(OutputStream out, Integer v) throws Exception {
        out.write(v);
    }

    @Before
    public void setup() {
        Path p = Paths.get(storeDir.getRoot().getPath());
        store = new TaskIdPathStore<>(p, BaseStringId::new);
        target = new KeyValueFileStore<>(store,
                                         unchecked(this::reader),
                                         unchecked(this::writer));
    }

    @Test
    public void putValue() throws Exception {
        BaseStringId key = new BaseStringId();
        int value = 123;
        target.put(key, value);

        Path storedValue = store.pathFor(key);
        int actualValue = Files.newInputStream(storedValue).read();
        assertThat(actualValue, is(value));
    }

    @Test
    public void modifyValue() throws Exception {
        BaseStringId key = new BaseStringId();
        int value = 123;
        target.put(key, value);
        target.modify(key, x -> x + 1);

        Path storedValue = store.pathFor(key);
        int actualValue = Files.newInputStream(storedValue).read();
        assertThat(actualValue, is(value + 1));
    }

    @Test
    public void removeValue() throws Exception {
        BaseStringId key = new BaseStringId();
        target.put(key, 123);
        target.remove(key);

        Path storedValue = store.pathFor(key);
        assertFalse(Files.exists(storedValue));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullStore() {
        new KeyValueFileStore<>(null,
                                unchecked(this::reader),
                                unchecked(this::writer));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullReader() {
        new KeyValueFileStore<>(store, null, unchecked(this::writer));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() {
        new KeyValueFileStore<>(store, unchecked(this::reader), null);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullKey() {
        target.put(null, 1);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullValue() {
        target.put(new BaseStringId(), null);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullKey() {
        target.modify(null, x -> x);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullOperation() {
        target.modify(new BaseStringId(), null);
    }

    @Test (expected = NullPointerException.class)
    public void removeThrowsIfNullKey() {
        target.remove(null);
    }

}
